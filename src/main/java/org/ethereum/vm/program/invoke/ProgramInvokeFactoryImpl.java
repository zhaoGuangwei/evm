/**
 * Copyright (c) [2018] [ The Semux Developers ]
 * Copyright (c) [2016] [ <ether.camp> ]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.vm.program.invoke;

import java.math.BigInteger;

import org.ethereum.vm.DataWord;
import org.ethereum.vm.client.Block;
import org.ethereum.vm.client.BlockStore;
import org.ethereum.vm.client.Repository;
import org.ethereum.vm.client.Transaction;
import org.ethereum.vm.program.Program;
import org.ethereum.vm.util.HashUtil;

public class ProgramInvokeFactoryImpl implements ProgramInvokeFactory {

    @Override
    public ProgramInvoke createProgramInvoke(Transaction tx, Block block, Repository repository,
            BlockStore blockStore) {

        byte[] address = tx.isCreate() ? HashUtil.calcNewAddress(tx.getFrom(), tx.getNonce()) : tx.getTo();
        byte[] origin = tx.getFrom();
        byte[] caller = tx.getFrom();
        long gas = tx.getGas();
        BigInteger gasPrice = tx.getGasPrice();
        BigInteger callValue = tx.getValue();
        byte[] callData = tx.getData();

        byte[] prevHash = block.getParentHash();
        byte[] coinbase = block.getCoinbase();
        long timestamp = block.getTimestamp();
        long number = block.getNumber();
        BigInteger difficulty = block.getDifficulty();
        long gasLimit = block.getGasLimit();

        Repository originalRepository = repository.clone();

        return new ProgramInvokeImpl(DataWord.of(address), DataWord.of(origin), DataWord.of(caller),
                gas, DataWord.of(gasPrice), DataWord.of(callValue), callData,
                DataWord.of(prevHash), DataWord.of(coinbase), DataWord.of(timestamp), DataWord.of(number),
                DataWord.of(difficulty), DataWord.of(gasLimit),
                repository, originalRepository, blockStore, 0, false);
    }

    @Override
    public ProgramInvoke createProgramInvoke(Program program,
            DataWord callerAddress, DataWord toAddress,
            long gas, DataWord value, byte[] data,
            Repository repository, BlockStore blockStore, boolean isStaticCall) {

        DataWord origin = program.getOriginAddress();
        DataWord gasPrice = program.getGasPrice();

        DataWord prevHash = program.getPrevHash();
        DataWord coinbase = program.getCoinbase();
        DataWord timestamp = program.getTimestamp();
        DataWord number = program.getNumber();
        DataWord difficulty = program.getDifficulty();
        DataWord gasLimit = program.getGasLimit();

        Repository originalRepository = program.getOriginalRepository();

        return new ProgramInvokeImpl(toAddress, origin, callerAddress, gas, gasPrice, value, data,
                prevHash, coinbase, timestamp, number, difficulty, gasLimit,
                repository, originalRepository, blockStore, program.getCallDepth() + 1, isStaticCall);
    }
}
