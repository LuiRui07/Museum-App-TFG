const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const beaconSchema = require("../models/beacon.js");


// Get All 
router.get("/", (req, res) => {
    beaconSchema
      .find()
      .then((data) => res.json(data))
      .catch((error) => res.json({ message: error }));
  });


  module.exports = router;