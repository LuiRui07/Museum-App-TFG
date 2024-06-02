const mongoose = require("mongoose");

const usersSchema = new mongoose.Schema({
    user: {
        type: String,
        required: true
    },
    mail: {
        type: String,
        required: true,
    },
    password: {
        type: String,
        required: false,
    },
    isGoogleUser: {
        type: Boolean,
        default: false
    }
});

module.exports = mongoose.model("user", usersSchema);
