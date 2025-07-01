/** @type import('hardhat/config').HardhatUserConfig */
require("@nomiclabs/hardhat-waffle")
require('dotenv').config();

const ALCHEMY_API_KEY="JcPFX8oSz6HlO9uiCVbJUd0MFDwrCFqF";
const PRIVATE_KEY="710ffc3a197b50e51bb9da1f8ea43401a8dc4fe482d10bd04a9134144c51abb5";
module.exports = {
  solidity: "0.8.9",
  networks: {
    sepolia: {
      url: `https://eth-sepolia.g.alchemy.com/v2/${ALCHEMY_API_KEY}`,
      accounts: [PRIVATE_KEY],
    },
    hardhat: {
      // Allow connection from Android emulator (or other devices on the same network)
      // via 0.0.0.0
      // NOTE: The Android emulator uses 10.0.2.2 as localhost.
      // This is configured in the Android app.
      hostname: "0.0.0.0",
    }
  },
}
