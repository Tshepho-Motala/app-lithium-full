ALTER TABLE `transaction`
    ADD CONSTRAINT `fk_payment_method_id` FOREIGN KEY (`payment_method_id`) REFERENCES `processor_user_card`(`id`);
