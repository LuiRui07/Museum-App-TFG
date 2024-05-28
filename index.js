const express = require("express");
const cors = require("cors");
const mongoose = require("mongoose")
const app = express();

const port = 5001;
app.use(express.json());
app.use(cors());

const authMiddleware = require("./middleware");
app.use(authMiddleware);  

app.use('/users', require("./routes/usersRoutes"));
app.use('/art', require('./routes/artRoutes'))
app.use('/beacon', require('./routes/beaconsRoutes'));
app.use("/museum", require("./routes/museumRoutes"));
app.use("/route", require("./routes/routeRoutes"));

mongoose.connect("mongodb+srv://ei:ei@cluster0.1acabfy.mongodb.net/Museum").then(() =>
    console.log("Hemos conectado con mongoDB")
  ).catch((error) =>
    console.error(error)
);


app.get("/", (req, res) => {
    res.send("Esta es la API");
});

app.listen(port, console.log("Servidor escuchando en el puerto ", port));