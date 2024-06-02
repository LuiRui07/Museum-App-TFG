const mongoose = require("mongoose")


const museumSchema = new mongoose.Schema({
    map: {
        type: String,
        required: true
    },
    name: {
        type: String,
        required: true
    },
    image: {
        type: String,
        required: true
    },
    location: {
        type: { type: String, enum: ['Point'], required: true },
        coordinates: { type: [Number], required: true }
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

museumSchema.index({ location: '2dsphere' });
module.exports = mongoose.model("museums", museumSchema);