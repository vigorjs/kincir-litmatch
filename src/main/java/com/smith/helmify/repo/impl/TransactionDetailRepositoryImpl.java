package com.smith.helmify.repo.impl;

import com.smith.helmify.model.meta.Machine;
import com.smith.helmify.model.meta.Service;
import com.smith.helmify.model.meta.Transaction;
import com.smith.helmify.model.meta.TransactionDetail;
import com.smith.helmify.repo.TransactionDetailRepository;
import com.smith.helmify.service.MachineService;
import com.smith.helmify.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionDetailRepositoryImpl implements TransactionDetailRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MachineService machineService;
    private final ServiceService serviceService;

    @Override
    public TransactionDetail save(TransactionDetail transactionDetail) {
        String sql = "INSERT INTO transaction_details (machine_id, service_id, transaction_id, amount, quantity) VALUES (?, ?, ?, ?, ?) RETURNING id, machine_id, service_id, transaction_id, amount, quantity";
        return jdbcTemplate.queryForObject(sql, new Object[]{
                transactionDetail.getMachine().getId(),
                transactionDetail.getService().getId(),
                transactionDetail.getTransaction().getId(),
                transactionDetail.getAmount(),
                transactionDetail.getQuantity()
        }, new TransactionDetailRowMapper(machineService, serviceService));
    }

    @Override
    public Optional<TransactionDetail> findById(Integer id) {
        String sql = "SELECT * FROM transaction_details WHERE id = ?";
        try {
            TransactionDetail transactionDetail = jdbcTemplate.queryForObject(sql, new Object[]{id}, new TransactionDetailRowMapper(machineService, serviceService));
            return Optional.ofNullable(transactionDetail);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<TransactionDetail> findByTransactionId(Integer transactionId) {
        String sql = "SELECT * FROM transaction_details WHERE transaction_id = ?";
        return jdbcTemplate.query(sql, new Object[]{transactionId}, new TransactionDetailRowMapper(machineService, serviceService));
    }

    @Override
    public List<TransactionDetail> findAll() {
        String sql = "SELECT * FROM transaction_details";
        return jdbcTemplate.query(sql, new TransactionDetailRowMapper(machineService, serviceService));
    }

    @Override
    public void update(TransactionDetail transactionDetail) {
        String sql = "UPDATE transaction_details SET machine_id=?, service_id = ?, transaction_id = ?, amount = ?, quantity = ? WHERE id = ?";
        jdbcTemplate.update(sql, transactionDetail.getMachine().getId(), transactionDetail.getService().getId(), transactionDetail.getTransaction().getId(), transactionDetail.getAmount(), transactionDetail.getQuantity(), transactionDetail.getId());
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM transaction_details WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class TransactionDetailRowMapper implements RowMapper<TransactionDetail> {

        private final MachineService machineService;
        private final ServiceService serviceService;

        public TransactionDetailRowMapper(MachineService machineService, ServiceService serviceService) {
            this.machineService = machineService;
            this.serviceService = serviceService;
        }

        @Override
        public TransactionDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
            Machine machine = machineService.getById(rs.getString("machine_id"));
            Service service = serviceService.serviceDTOToService(serviceService.getById(rs.getInt("service_id")));

            return TransactionDetail.builder()
                    .id(rs.getInt("id"))
                    .machine(machine)
                    .service(service)
                    .transaction(Transaction.builder().id(rs.getInt("transaction_id")).build())
                    .amount(rs.getLong("amount"))
                    .quantity(rs.getInt("quantity"))
                    .build();
        }
    }
}