const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const ObjectId = mongoose.Types.ObjectId;
const usuariosSchema = require("../models/usuarios.js");
//LLAMADAS CRUD-------------------------------------------------------------------------------

//Create (Revisar)
router.post("/", (req, res) => {
  const user = usuariosSchema(req.body);
  axios.get('http://localhost:5001/usuarios/correo/' + user.correo).then((response) => {
    const { data } = response;
    const { message } = data;
    if (message === "No se ha encontrado ningún usuario con ese correo.") {
      user
        .save()
        .then((data) => res.json(data))
        .catch((error) => res.json({ message: error }));
    } else {
      return res.json({ message: "Ya existe un usuario con ese correo." });
    }
  }
  ).catch((error) => res.json({ message: error }));
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
        res.json({ message: "Usuario logueado correctamente"});
      } else {
        res.json({ message: "Correo o contraseña incorrectos." });
      }
    })
    .catch((error) => res.json({ message: error }));
});


module.exports = router