const mongoose = require("mongoose")


const mapsSchema = new mongoose.Schema({
    geojson: {
        type: Object,
        required: true,
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
module.exports = mongoose.model("maps", mapsSchema);