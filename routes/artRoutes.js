const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const ObjectId = mongoose.Types.ObjectId;
const artSchema = require("../models/art.js");
const axios = require('axios');

//LLAMADAS CRUD-------------------------------------------------------------------------------

//Create
router.post("/", (req, res) => {
  const obra = artSchema(req.body);
    obra.save()
        .then(() => {
        res.json({ message: "1" }); // Responde una vez que la obra se ha guardado correctamente
        })
        .catch((error) => res.json({ message: error }));
});

// Get All 
router.get("/", (req, res) => {
  artSchema
    .find()
    .then((data) => res.json(data))
    .catch((error) => res.json({ message: error }));
});

// Get by ID 
router.get("/:id", (req, res) => {
    const { id } = req.params;
    artSchema
        .findById({_id: id})
        .then((data) => {
        if (data) {
            res.json(data);
        } else {
            res.json({ message: "No se ha encontrado ninguna obra con ese id." });
        }
        })
        .catch((error) => res.json({ message: error }));
    }
);

// Delete 
router.delete("/:id", (req, res) => {
    const { id } = req.params;
    artSchema
        .deleteOne({ _id: id })
        .then((data) => res.json(data))
        .catch((error) => res.json({ message: error }));
    }
);

// Update 
router.put("/:id", (req, res) => {
    const { id } = req.params;
    artSchema
        .updateOne({ _id: id
        }, {
            $set: req.body
        })
        .then((data) => res.json(data))
        .catch((error) => res.json({ message: error }));
    }
);

//------------------------------------------------------------------------------------

module.exports = router;