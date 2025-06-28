const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("Lottery Contract", function () {
  let Lottery, lottery, manager, addr1, addr2, addr3;

  beforeEach(async () => {
    [manager, addr1, addr2, addr3] = await ethers.getSigners();
    Lottery = await ethers.getContractFactory("Lottery");
    lottery = await Lottery.connect(manager).deploy();
    await lottery.deployed();
  });

  it("should set the manager correctly", async () => {
    expect(await lottery.manager()).to.equal(manager.address);
  });

  it("should accept exactly 2 ETH from participants", async () => {
    await lottery.connect(addr1).sendTransaction({
      to: lottery.address,
      value: ethers.utils.parseEther("2"),
    });

    const participant = await lottery.participants(0);
    expect(participant).to.equal(addr1.address);
  });

  it("should reject incorrect ETH amount", async () => {
    await expect(
      lottery.connect(addr1).sendTransaction({
        to: lottery.address,
        value: ethers.utils.parseEther("1"), // incorrect
      })
    ).to.be.revertedWith("Must send exactly 2 ETH");
  });

  it("should only allow manager to see balance", async () => {
    await lottery.connect(addr1).sendTransaction({
      to: lottery.address,
      value: ethers.utils.parseEther("2"),
    });

    await expect(lottery.connect(addr1).getBalance()).to.be.revertedWith("Only manager can view balance");

    const balance = await lottery.connect(manager).getBalance();
    expect(balance).to.equal(ethers.utils.parseEther("2"));
  });

  it("should only allow manager to select winner after 3 participants", async () => {
    for (let user of [addr1, addr2, addr3]) {
      await lottery.connect(user).sendTransaction({
        to: lottery.address,
        value: ethers.utils.parseEther("2"),
      });
    }

    const initialBalances = await Promise.all(
      [addr1, addr2, addr3].map(a => ethers.provider.getBalance(a.address))
    );

    const tx = await lottery.connect(manager).selectWinner();
    await tx.wait();

    const finalBalances = await Promise.all(
      [addr1, addr2, addr3].map(a => ethers.provider.getBalance(a.address))
    );

    const winnerIndex = finalBalances.findIndex((b, i) =>
      b.gt(initialBalances[i].add(ethers.utils.parseEther("5.8")))
    );
    expect(winnerIndex).to.not.equal(-1);

    const contractBalance = await lottery.getBalance();
    expect(contractBalance).to.equal(0);
  });

  it("should revert selectWinner if called by non-manager", async () => {
    await expect(lottery.connect(addr1).selectWinner()).to.be.revertedWith("Only manager can select winner");
  });

  it("should revert selectWinner if less than 3 participants", async () => {
    await lottery.connect(addr1).sendTransaction({
      to: lottery.address,
      value: ethers.utils.parseEther("2"),
    });
    await lottery.connect(addr2).sendTransaction({
      to: lottery.address,
      value: ethers.utils.parseEther("2"),
    });

    await expect(lottery.connect(manager).selectWinner()).to.be.revertedWith("Not enough participants");
  });
});
