const mongoose = require("mongoose")


const usuariosSchema = new mongoose.Schema({
    user: {
        type: String,
        required: true
    },
    mail: {
        type: String,
        required: true,
        unique: true
    },
    password: {
        type: String,
        required: true
    }
    
});
module.exports = mongoose.model("users", usuariosSchema);