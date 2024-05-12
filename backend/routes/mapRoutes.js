const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const ObjectId = mongoose.Types.ObjectId;
const mapSchema = require("../models/map.js");

// Get All 
router.get("/", (req, res) => {
    mapSchema
      .find()
      .then((data) => res.json(data))
      .catch((error) => res.json({ message: error }));
  });

router.get("/:id", (req, res) => {
    mapSchema
      .findById(req.params.id)
      .then((data) => res.json(data))
      .catch((error) => res.json({ message: error }));
  });

router.post("/", (req, res) => {
  const map = mapSchema(req.body);
    map.save()
        .then(() => {
        res.json({ message: "1" }); // Responde una vez que el mapa se ha guardado correctamente
        })
        .catch((error) => res.json({ message: error }));
});

module.exports = router;


