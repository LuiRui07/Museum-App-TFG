const mongoose = require('mongoose');

const artSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true
    },
    description: {
        type: String,
        required: true
    },
    author: {    
        type: String,
        required: true
    },
    date: {
        type: String,
        required: false
    },
    century: {
        type: String,
        required: false
    },
    image: {
        type: String,
        required: true
    },
    category: {
        type: String,
        required: true
    },
    museum: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'museums',
        required: true
    }
});

module.exports = mongoose.model("art", artSchema);