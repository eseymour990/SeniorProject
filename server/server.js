var express = require("express");
var mysql = require("mysql");
var jwt = require('jsonwebtoken');
var bodyParser = require('./config.js');
var app = express();
var config = require('./config.js');
app = config(app);
var CryptoJS = require("crypto-js");

var con = mysql.createConnection({
	user: "eseymour",
	host : "localhost",
	password: "fredfrdeburger",
	database: "eseymour",
	insecureAuth: true
});

con.connect(function(err) {
	  if (err) 
		console.log("err " + err);
	  console.log("Connected!");
});

app.listen(8002, () => {
	console.log("Server running on port 8002");
});

app.get("/AddUser", (req, res, next) => {
	if(!req.headers.authorization || req.headers.authorization.indexOf('Basic') === -1){
		return res.status(401).json({message: 'No Auth'});
	}
	console.log(req.headers.authorization);
	const base64Cred = req.headers.authorization.split(' ')[1];
	const cred = Buffer.from(base64Cred, 'base64').toString('ascii');
	const creds = cred.split(':');
	console.log(creds[0] + "    " + creds[1]);
	var sqlCheckUsername = "Select * from Login where Username = ?"
	var value = CryptoJS.MD5(creds[0]).toString();
	con.query(sql, values, function(err, result){
		if(err.code == 'ER_EMPTY_QUERY')
		{
			console.log("HERE");
		}
		else if(err.code != 'ER_EMPTY_QUERY'){console.log("HERE2");throw err;}
		else{
			return res.status(400).json({message: 'Username Already taken'})
		}
	});
		
	
	var sql = "INSERT INTO Login (Username, Password) VALUES (?,?)";
	var values = [CryptoJS.MD5(creds[0]).toString(), CryptoJS.MD5(creds[1]).toString()];
	con.query(sql, values, function(err, result){
		
		if(err) {throw err};
		console.log("Records inserted: " + result.affectedRows);
	});
	var signedToken = signToken(creds[0]);
	return res.status(200).json({message: 'good', token: signedToken});;

});       

function signToken(Username){
	return jwt.sign({
		data: Username
	}, 'testSecret', { expiresIn: '24h'});
}

app.get("/AuthUser", (req,res,next) => {
        if(!req.headers.authorization || req.headers.authorization.indexOf('Basic')
	=== -1){
	                return res.status(401).json({message: 'No Auth'});
	        }
        const base64Cred= req.headers.authorization.split(' ')[1];
        const cred = Buffer.from(base64Cred, 'base64').toString('ascii');
        const creds = cred.split(':');
	console.log(creds[0] + "   " + creds[1]);
	var sql = "SELECT Password FROM Login WHERE Username = ?";
	var values = [CryptoJS.MD5(creds[0]).toString()];
	con.query(sql, values, function(err, result){
		if(err) throw err;
		console.log(result[0].Password + " <- result / inputed Pass -> " + CryptoJS.MD5(creds[1]));
		if(result[0] == null)
			return res.status(404).json({message : "Username not valid"});
		if(result[0].Password == CryptoJS.MD5(creds[1]).toString())
		{
			console.log("Found User");
       	 		var signedToken = signToken(creds[0]);
			return res.status(200).json({message: 'good', token: signedToken});
		}
		else
			return res.status(401).json({message : "Bad"});
	});
	
});

app.get("/AddLure", (req,res,next) => {
	//verify user is authed
	if(!IsAuth(req)){
		return res.status(401).json({message : "Not Auth"});
	}
	console.log("add Lure");
});

function IsAuth(req)
{
	if(!req.headers.authorization || req.headers.authorization.indexOf('Bearer') === -1){
		return false;
	}
	const base64Cred = req.headers.authorization.split(' ')[1];
	const cred = Buffer.from(base64Cred, 'base64').toString('ascii');
	try{
		var decode = jwt.verify(cred, 'testSecret');
	}catch{
		return false;
	}
	return true;
}

app.get("/GetLures", (req, res, next) => {
	if(!req.headers.authorization || req.headers.authorization.indexOf('Bearer') === -1){
		return res.status(401).json({message: 'No Auth'});
	}
	const base64Cred= req.headers.authorization.split(' ')[1];
	const cred = Buffer.from(base64Cred, 'base64').toString('ascii');
	try{
	var decode = jwt.verify(cred, 'testSecret');
	}catch{
		return res.status(401).json({message : "Not Authorized"});
	}
	var sql = "select G.Name, L.DiveEquation from Lure L inner join Gear G on L.ID = G.ID inner join gearType GT on G.GearType = GT.id where GT.Type = 'Diving lure' or GT.Type = 'Flat lure';";
        con.query(sql, function(err, result){
	        if(err) throw err;
                console.log(result[0]);
		console.log(result.length);
		console.log(result[0].Name);
        });

});

//app.post("/postTest", (req, res, next) => {
//	console.log(req.body);
//	console.log(res.body)
//	res.json(["works"]);
//});


//app.post("/login", req, res, next) => {
	
//	console.log(req);
//});
