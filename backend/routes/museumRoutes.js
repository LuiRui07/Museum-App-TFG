const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const ObjectId = mongoose.Types.ObjectId;
const museumSchema = require("../models/museum.js");



//LLAMADAS CRUD-------------------------------------------------------------------------------

//Create
router.post("/", (req, res) => {
  const museum = museumSchema(req.body);
    museum.save()
        .then(() => {
        res.json({ message: "1" }); // Responde una vez que el mapa se ha guardado correctamente
        })
        .catch((error) => res.json({ message: error }));
});

// Get All 
router.get("/", (req, res) => {
    museumSchema
      .find()
      .then((data) => res.json(data))
      .catch((error) => res.json({ message: error }));
  });

// Get by ID 
router.get("/:id", (req, res) => {
  museumSchema
      .findById(req.params.id)
      .then((data) => res.json(data))
      .catch((error) => res.json({ message: error }));
  });



// Delete 
router.delete("/:id", (req, res) => {
  const { id } = req.params;
  museumSchema
      .deleteOne({ _id: id })
      .then((data) => res.json(data))
      .catch((error) => res.json({ message: error }));
  }
);

// Update 
router.put("/:id", (req, res) => {
  const { id } = req.params;
  museumSchema
      .updateOne({ _id: id
      }, {
          $set: req.body
      })
      .then((data) => res.json(data))
      .catch((error) => res.json({ message: error }));
  }
);


//--------------------------------------------

// Obtener Un Museo por sus Coordenadas
router.get("/fromCoords/:lat/:lon", (req, res) => {
  const tolerance = 0.01; // Define la tolerancia permitida en grados (ajusta según tus necesidades)
  
  // Obtén las coordenadas del parámetro de la solicitud
  const { lat, lon } = req.params;

  // Calcula el rango para las coordenadas latitud y longitud
  const latRange = [parseFloat(lat) - tolerance, parseFloat(lat) + tolerance];
  const lonRange = [parseFloat(lon) - tolerance, parseFloat(lon) + tolerance];

  // Realiza la búsqueda en la base de datos utilizando el operador $and para combinar las condiciones
  museumSchema
    .findOne({
      $and: [
        { lat: { $gte: latRange[0], $lte: latRange[1] } }, // Latitud dentro del rango
        { lon: { $gte: lonRange[0], $lte: lonRange[1] } }  // Longitud dentro del rango
      ]
    })
    .then((data) => res.json(data))
    .catch((error) => res.json({ message: error }));
});



module.exports = router;


