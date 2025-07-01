const { ethers } = require("hardhat");

async function testContract() {
    console.log("Testing Lottery Contract...");
    
    // Get the deployed contract
    const Lottery = await ethers.getContractFactory("Lottery");
    const contract = Lottery.attach("0x5FbDB2315678afecb367f032d93F642f64180aa3");
    
    // Get the manager
    const manager = await contract.manager();
    console.log("Manager:", manager);
    
    // Get the balance
    const balance = await contract.getBalance();
    console.log("Contract Balance:", ethers.utils.formatEther(balance), "ETH");
    
    console.log("Contract test completed successfully!");
}

testContract()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    }); 