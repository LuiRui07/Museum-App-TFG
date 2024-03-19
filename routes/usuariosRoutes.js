const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const ObjectId = mongoose.Types.ObjectId;
const usuariosSchema = require("../models/usuarios.js");
//LLAMADAS CRUD-------------------------------------------------------------------------------

//Create
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
router.get("/:id", (req, res) => {
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

// delete, comprobado con Postman 
router.delete("/:id", (req, res) => {
  const { id } = req.params;
  usuariosSchema
    .deleteOne({ _id: id })
    .then((data) => res.json(data))
    .catch((error) => res.json({ message: error }));
});

// update, comprobado con Postman
router.put("/:id", (req, res) => {
  const { id } = req.params;
  const { nombreCompleto, calle, numero, codigoPostal, ciudad, provincia, pais, valoraciones, numeroValoraciones} = req.body;
  usuariosSchema
    .updateOne({ _id: id }, { $set: { nombreCompleto, calle, numero, codigoPostal, ciudad, provincia, pais,valoraciones, numeroValoraciones} })
    .then((data) => res.json(data))
    .catch((error) => res.json({ message: error }));
});




module.exports = router