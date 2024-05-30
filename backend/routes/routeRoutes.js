const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const ObjectId = mongoose.Types.ObjectId;
const routesSchema = require("../models/route.js");


//LLAMADAS CRUD-------------------------------------------------------------------------------
//Create
router.post("/", (req, res) => {
    const route = routesSchema(req.body);
    console.log(req.body);  
      route.save()
          .then(() => {
          res.json({ message: "1" }); // Responde una vez que el mapa se ha guardado correctamente
          })
          .catch((error) => res.json({ message: error }));
  });
  
  // Get All 
  router.get("/", (req, res) => {
    routesSchema
        .find()
        .then((data) => res.json(data))
        .catch((error) => res.json({ message: error }));
    });
  
  // Get by ID 
  router.get("/:id", (req, res) => {
    routesSchema
        .findById(req.params.id)
        .then((data) => res.json(data))
        .catch((error) => res.json({ message: error }));
    });
  
  
  
  // Delete 
  router.delete("/:id", (req, res) => {
    const { id } = req.params;
    routesSchema
        .deleteOne({ _id: id })
        .then((data) => res.json(data))
        .catch((error) => res.json({ message: error }));
    }
  );
  
  // Update 
  router.put("/:id", (req, res) => {
    const { id } = req.params;
    routesSchema
        .updateOne({ _id: id
        }, {
            $set: req.body
        })
        .then((data) => res.json(data))
        .catch((error) => res.json({ message: error }));
    }
  );

  // -------------------------------------------------------------------------------------------

  // Get Routes From User
  router.get("/user/:id", (req, res) => {
    routesSchema
        .find({user: req.params.id})
        .then((data) => res.json(data))
        .catch((error) => res.json({ message: error }));
    });

module.exports = router;