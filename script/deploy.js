const {ethers} = require("hardhat");
const fs = require('fs');

async function main(){
    const [deployer]=await ethers.getSigners();
     console.log("Deploying contract with address:", deployer.address);

    const Lottery=await ethers.getContractFactory("Lottery");
    const token=await Lottery.deploy();
    await token.deployed();
    
    console.log("Lottery Address" , token.address);

    // Write contract address and ABI to a JSON file for Android app
    const contractInfo = {
        contractAddress: token.address,
        abi: Lottery.abi,
        networkUrl: "https://eth-sepolia.g.alchemy.com/v2/JcPFX8oSz6HlO9uiCVbJUd0MFDwrCFqF", // For Android emulator
        chainId: 11155111 // Hardhat network chain ID
    };

    // Save to Android project assets directory
    const androidAssetsPath = 'C:/Users/Genius/AndroidStudioProjects/MyApplication7/app/src/main/assets';
    const outputFilename = 'contract_info.json';

    // Ensure the output directory exists
    fs.mkdirSync(androidAssetsPath, { recursive: true });

    // Write the file
    fs.writeFileSync(`${androidAssetsPath}/${outputFilename}`, JSON.stringify(contractInfo, null, 2));
    console.log(`Contract address and ABI saved to: ${androidAssetsPath}/${outputFilename}`);

    // Also save to local android-integration directory for reference
    const localAssetsPath = 'android-integration/src/main/assets';
    fs.mkdirSync(localAssetsPath, { recursive: true });
    fs.writeFileSync(`${localAssetsPath}/${outputFilename}`, JSON.stringify(contractInfo, null, 2));
    console.log(`Contract address and ABI also saved to: ${localAssetsPath}/${outputFilename}`);
}

main()
.then(()=>process.exit(0))
.catch((error)=>{
    console.error(error);
    process.exit(1);

});