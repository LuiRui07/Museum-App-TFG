const mongoose = require("mongoose")


const museumSchema = new mongoose.Schema({
    geojson: {
        type: Object,
        required: true,
    },
    name: {
        type: String,
        required: true
    },
    image: {
        type: String,
        required: true
    },
    lat: {
        type: Number,
        required: true
    },
    lon: {
        type: Number,
        required: true
    }
    
});
module.exports = mongoose.model("museums", museumSchema);