package edu.iu.habahram.coffeeorder.repository;

import edu.iu.habahram.coffeeorder.model.*;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@Repository
public class OrderRepository {
    private static final String DATABASE_FILE = "db.txt";

    public Receipt add(OrderData order) throws Exception {
        Beverage beverage = null;
        switch (order.beverage().toLowerCase()) {
            case "dark roast":
                beverage = new DarkRoast();
                break;
            case "house blend":
                beverage = new HouseBlend();
                break;
            case "espresso":
                beverage = new Espresso();
                break;
            case "decaf":
                beverage = new Decaf();
                break;
            default:
                throw new Exception("Beverage type '%s' is not valid!".formatted(order.beverage()));
        }

        for (String condiment : order.condiments()) {
            switch (condiment.toLowerCase()) {
                case "milk":
                    beverage = new Milk(beverage);
                    break;
                case "mocha":
                    beverage = new Mocha(beverage);
                    break;
                case "whip":
                    beverage = new Whip(beverage);
                    break;
                case "soy":
                    beverage = new Soy(beverage);
                    break;
                default:
                    throw new Exception("Condiment type '%s' is not valid".formatted(condiment));
            }
        }

        Receipt receipt = new Receipt(UUID.randomUUID().hashCode(), beverage.getDescription(), beverage.cost());
        storeOrderInDatabase(receipt);
        return receipt;
    }

    private void storeOrderInDatabase(Receipt receipt) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE, true))) {
            String orderLine = String.format("%d,%.2f,%s", receipt.id(), receipt.cost(), receipt.description());
            writer.write(orderLine);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}