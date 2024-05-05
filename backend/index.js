const express = require("express");
const cors = require("cors");
const mongoose = require("mongoose")
const app = express();

const port = 5001;
app.use(express.json());
app.use(cors());

const usersRoutes = require("./routes/usersRoutes");
const artRoutes = require("./routes/artRoutes");
app.use('/users', usersRoutes);
app.use('/art', artRoutes);
app.use('/beacon', require('./routes/beaconsRoutes'));

mongoose.connect(
  "mongodb+srv://ei:ei@cluster0.1acabfy.mongodb.net/Museum").then(() =>
    console.log("Hemos conectado con mongoDB")
  ).catch((error) =>
    console.error(error)
);


app.get("/", (req, res) => {
    res.send("Esta es la API");
});

app.listen(port, console.log("Servidor escuchando en el puerto ", port));