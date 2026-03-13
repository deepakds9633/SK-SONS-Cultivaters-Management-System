package com.sksons.cultivaters.service;

import com.sksons.cultivaters.entity.Client;
import com.sksons.cultivaters.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client clientDetails) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        client.setName(clientDetails.getName());
        client.setPhone(clientDetails.getPhone());
        client.setVillage(clientDetails.getVillage());
        client.setFieldDetails(clientDetails.getFieldDetails());
        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public long getTotalClients() {
        return clientRepository.count();
    }

    // Recalculate client balances from all records
    public void recalculateClientBalance(Long clientId) {
        // This will be triggered from PaymentService and WorkEntryService
    }
}
