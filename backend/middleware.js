const jwt = require('jsonwebtoken');


function authMiddleware(req, res, next) {

    /*
    // Payload del token (puede ser cualquier información que desees incluir)
    const payload = {
    userId: '123456789',
    username: 'ejemplo_usuario',
    role: 'admin'
    };

    // Firma del token con la clave secreta
    const token1 = jwt.sign(payload, 'ClaveSecreta');

    console.log(token1);
    */
   
    const token = req.headers.authorization;
    console.log(token)
    if (!token) {
    return res.status(401).json({message: 'Authentication failed' });
    }

    try {
    const decoded = jwt.verify(token, "ClaveSecreta");
    console.log(decoded);
    req.userId = decoded.userId;
    next();
    } catch (err) {
    res.status (401).json({ message: 'Authentication failed' });
    }
} 

module.exports = authMiddleware;