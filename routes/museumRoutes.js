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
  const { lat, lon } = req.params;

  const latitude = parseFloat(lat);
  const longitude = parseFloat(lon);

  // Define el punto de consulta en el formato GeoJSON
  const point = {
    type: "Point",
    coordinates: [longitude, latitude]
  };

  // Realiza la búsqueda utilizando $geoNear
  museumSchema.aggregate([
    {
      $geoNear: {
        near: point,
        distanceField: "dist.calculated",
        maxDistance: 200, // 200 metros
        spherical: true
      }
    },
    {
      $sort: { "dist.calculated": 1 }
    },
    {
      $limit: 1
    },
    {
      $project: {
        dist: 0 // Excluir el campo 'dist'
      }
    }
  ])
  .then(data => res.json(data[0] || {}))
  .catch(error => res.json({ message: error }));
});




module.exports = router;


