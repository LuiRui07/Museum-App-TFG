const mongoose = require("mongoose")


const beaconsSchema = new mongoose.Schema({
    uuid : {
        type: String,
        required: true,
    },
    major: {
        type: Number,
        required: true,
    },
    minor: {
        type: Number,
        required: true
    },
    museum : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'museum',
    },
    
});
module.exports = mongoose.model("beacon", beaconsSchema);