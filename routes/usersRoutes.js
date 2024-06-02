const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const ObjectId = mongoose.Types.ObjectId;
const usersSchema = require("../models/user.js");
const axios = require('axios');

/// Esta en este orden para evitar problemas
/* Comprobar que no existe correo y/o usuario */
router.get("/check", (req, res) => {
  const mail = req.query.mail.toLowerCase(); 
  const user = req.query.user; 

  let correoExists = false;
  let userExists = false;

  usersSchema
    .find({ mail: mail, isGoogleUser: false})
    .then((dataCorreo) => {
      if (dataCorreo && dataCorreo.length > 0) { 
        correoExists = true;
      }

      usersSchema
        .find({ user: user, isGoogleUser: false})
        .then((dataUser) => {
          if (dataUser && dataUser.length > 0) { 
            userExists = true;
          }

          if (correoExists) {
            res.json({ message: "Ya existe ese correo" });
    
          } else if (userExists) {
            res.json({ message: "Ya existe ese nombre de usuario" });
          } else {
            res.json({ message: "1" });
          }
        })
        .catch((error) => res.json({ message: error }));
    })
    .catch((error) => res.json({ message: error }));
});

/* Login */
router.post("/login", (req, res) => {
  const { user, password } = req.body;
  const mail = user.toLowerCase();
  usersSchema
    .findOne({ 
      $or: [{ mail: mail }, { user: user }],
      password: password,
      isGoogleUser: false
     })
    .then((data) => {
      if (data) {
        // Si se encuentra el usuario, devolver además de "1", el user y el mail
        res.json({ message: "1", user: data});
      } else {
        // Si no se encuentra el usuario, devolver "0"
        res.json({ message: "0" });
      }
    })
    .catch((error) => res.json({ message: error }));
}); 

router.post("/google", (req, res) => {
  const { mail } = req.query;
  usersSchema
    .findOne({ mail: mail, isGoogleUser: true })
    .then((data) => {
      if (data) {
        res.json({ message: "1", user: data});
      } else {
        const newUser = new usersSchema({
          user: req.query.user, // Asegúrate de que user se envíe en req.body
          mail: mail.toLowerCase(),
          password: "",
          isGoogleUser: req.query.photo,
        });
        newUser.save() 
        .then(() => {
          usersSchema.findOne({ mail: mail, isGoogleUser: true })
          .then((userData) => {
            res.json({ message: "1", user: userData });
          })
          .catch((error) => res.json({ message: error }));
        })
        .catch((error) => res.json({ message: error }));
      }
    })
    .catch((error) => res.json({ message: error }));
});
 

//LLAMADAS CRUD-------------------------------------------------------------------------------

// Create 
router.post("/", async (req, res) => {
  try {
    const user = usersSchema(req.body);
    user.mail = user.mail.toLowerCase();
    const token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NTY3ODkiLCJ1c2VybmFtZSI6ImVqZW1wbG9fdXN1YXJpbyIsInJvbGUiOiJhZG1pbiIsImlhdCI6MTcxNTg1NDk4M30.VREIXyTuhEGiP5Zs3YKwjqPBFC41vwr6gkr1D2ogWUI"
    const response = await axios.get(`https://tfg-tkck.vercel.app/users/check?mail=${user.mail}&user=${user.user}`, {
      headers: {
        'Authorization': `${token}`
      }
    });

    const { message } = response.data; 
    console.log(user);
    console.log(message);
    
    if (message === "1") {
      user.save()
        .then(() => {
          res.json({ message: "1" }); // Responde una vez que el usuario se ha guardado correctamente
        })
        .catch((error) => res.json({ message: error }));
    } else {
      res.json({ message: "Ya existe un usuario con ese correo o usuario." }); // Responde si ya existe un usuario con ese correo
    }
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
});

// Get All
router.get("/", (req, res) => {
  usersSchema
    .find()
    .then((data) => res.json(data))
    .catch((error) => res.json({ message: error }));
});

// Get by ID 
router.get("/:id", (req, res) => {
  const { id } = req.params;
  usersSchema
    .findById({_id: id})
    .then((data) => {
      if (data) {
        res.json(data);
      } else {
        res.json({ message: "No se ha encontrado ningún usuario con ese id." });
      }
    })
    .catch((error) => res.json({ message: error }));
});

// Delete 
router.delete("/:id", (req, res) => {
  const { id } = req.params;
  usersSchema
    .deleteOne({ _id: id })
    .then((data) => res.json(data))
    .catch((error) => res.json({ message: error }));
});

// Update 
router.put("/:id", (req, res) => {
  const { id } = req.params;
  const {user, mail, password} = req.body;
  usersSchema
    .updateOne({ _id: id }, { $set: { user, mail, password} })
    .then((data) => res.json(data))
    .catch((error) => res.json({ message: error }));
});



module.exports = router