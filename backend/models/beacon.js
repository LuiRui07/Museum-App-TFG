const mongoose = require("mongoose")


const beaconsSchema = new mongoose.Schema({
    uuid : {
        type: String,
        required: true,
    },
    major: {
        type: String,
        required: true,
    },
    minor: {
        type: String,
        required: true
    },
    map : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'map',
    },
    
});
module.exports = mongoose.model("beacon", beaconsSchema);