const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const ObjectId = mongoose.Types.ObjectId;
const usuariosSchema = require("../models/usuarios.js");
const fetch = require("node-fetch");

//LLAMADAS CRUD-------------------------------------------------------------------------------

//Create (Revisar) // Que guarde el correo en minsuculas!!
router.post("/", (req, res) => {
  const user = usuariosSchema(req.body);
  user.mail = user.mail.toLowerCase();
  fetch(`http://localhost:5001/users/check?mail=${user.mail}&user=${user.user}`)
  .then((response) => response.json()) // Parsea la respuesta como JSON
  .then((data) => {
    const { message } = data;
    console.log(user);
    if (message === "1") {
      user.save()
        .then((data) => {
          res.json({ message: "Usuario creado correctamente." }); // Responde una vez que el usuario se ha guardado correctamente
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
  usuariosSchema
    .find()
    .then((data) => res.json(data))
    .catch((error) => res.json({ message: error }));
});

// Get by ID
router.get("/user/:id", (req, res) => {
  const { id } = req.params;
  usuariosSchema
    .findById(id)
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
  usuariosSchema
    .deleteOne({ _id: id })
    .then((data) => res.json(data))
    .catch((error) => res.json({ message: error }));
});

// Update (Revisar)
router.put("/:id", (req, res) => {
  const { id } = req.params;
  const { nombreCompleto, calle, numero, codigoPostal, ciudad, provincia, pais, valoraciones, numeroValoraciones} = req.body;
  usuariosSchema
    .updateOne({ _id: id }, { $set: { nombreCompleto, calle, numero, codigoPostal, ciudad, provincia, pais,valoraciones, numeroValoraciones} })
    .then((data) => res.json(data))
    .catch((error) => res.json({ message: error }));
});

//------------------------------------------------------------------------------------

// Login
router.post("/login", (req, res) => {
  const { user, password } = req.body;
  usuariosSchema
    .findOne({ user: user, password: password })
    .then((data) => {
      if (data) {
        res.json({ message: "1"});
      } else {
        res.json({ message: "0" });
      }
    })
    .catch((error) => res.json({ message: error }));
});

// Comprobar que no existe correo y/o usuario
router.get("/check", (req, res) => {
  const correo = req.query.mail.toLowerCase(); 
  const user = req.query.user; 

  let correoExists = false;
  let userExists = false;

  usuariosSchema
    .find({ mail: correo })
    .then((dataCorreo) => {
      if (dataCorreo && dataCorreo.length > 0) { 
        correoExists = true;
      }

      usuariosSchema
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


module.exports = router