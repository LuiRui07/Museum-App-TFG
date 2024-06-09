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
    beacons: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'beacon',
        required: false,
    }], 
});

museumSchema.index({ location: '2dsphere' });
module.exports = mongoose.model("museum", museumSchema);