const mongoose = require("mongoose")


const routesSchema = new mongoose.Schema({
    museum: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'museum',
        required: true,
    },
    arts: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'art',
        required: true,
    }],
    user : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'user',
        required: true
    },
});

module.exports = mongoose.model("route", routesSchema);