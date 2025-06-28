const {ethers} = require("hardhat");

async function main(){
    const [deployer]=await ethers.getSigners();
     console.log("Deploying contract with address:", deployer.address);

    const Lottery=await ethers.getContractFactory("Lottery");
    const token=await Lottery.deploy();
    await token.deployed();
    
    console.log("Lottery Address" , token.address);
}
main()
.then(()=>process.exit(0))
.catch((error)=>{
    console.error(error);
    process.exit(1);

});