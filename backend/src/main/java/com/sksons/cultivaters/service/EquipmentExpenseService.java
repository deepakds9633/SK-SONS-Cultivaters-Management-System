package com.sksons.cultivaters.service;

import com.sksons.cultivaters.entity.EquipmentExpense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
public class EquipmentExpenseService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Double getTotalExpenses() {
        TypedAggregation<EquipmentExpense> aggregation = newAggregation(EquipmentExpense.class,
            group().sum("amount").as("total")
        );
        AggregationResults<org.bson.Document> result = mongoTemplate.aggregate(aggregation, org.bson.Document.class);
        org.bson.Document doc = result.getUniqueMappedResult();
        if (doc != null && doc.get("total") != null) {
            return ((Number) doc.get("total")).doubleValue();
        }
        return 0.0;
    }
}
