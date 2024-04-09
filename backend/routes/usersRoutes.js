const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const ObjectId = mongoose.Types.ObjectId;
const usersSchema = require("../models/users.js");
const axios = require('axios');

/* Comprobar que no existe correo y/o usuario */
router.get("/check", (req, res) => {
  const mail = req.query.mail.toLowerCase(); 
  const user = req.query.user; 

  let correoExists = false;
  let userExists = false;

  usersSchema
    .find({ mail: mail })
    .then((dataCorreo) => {
      if (dataCorreo && dataCorreo.length > 0) { 
        correoExists = true;
      }

      usersSchema
        .find({ user: user })
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
      password: password
     })
    .then((data) => {
      if (data) {
        res.json({ message: "1"});
      } else {
        res.json({ message: "0" });
      }
    })
    .catch((error) => res.json({ message: error }));
});


//LLAMADAS CRUD-------------------------------------------------------------------------------

// Create 
router.post("/", (req, res) => {
  const user = usersSchema(req.body);
  user.mail = user.mail.toLowerCase();
  axios.get(`http://localhost:5001/users/check?mail=${user.mail}&user=${user.user}`)
    .then((response) => {
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
    })
    .catch((error) => res.json({ message: error }));
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