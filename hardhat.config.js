/** @type import('hardhat/config').HardhatUserConfig */
require("@nomiclabs/hardhat-waffle")

const ALCHEMY_API_KEY="JcPFX8oSz6HlO9uiCVbJUd0MFDwrCFqF";
const PRIVATE_KEY="710ffc3a197b50e51bb9da1f8ea43401a8dc4fe482d10bd04a9134144c51abb5";
module.exports = {
  solidity: "0.8.9",
  networks: {
    sepolia: {
      url: `https://eth-sepolia.g.alchemy.com/v2/${ALCHEMY_API_KEY}`,
      accounts: [PRIVATE_KEY],
    },
  },
}
