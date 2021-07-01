/*
 * This file is a part of project QuickShop, the name is PriceLimiter.java
 *  Copyright (C) PotatoCraft Studio and contributors
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.maxgamer.quickshop.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.QuickShop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@AllArgsConstructor
@Data
public class PriceLimiter {
    private double minPrice;
    private double maxPrice;
    private boolean allowFreeShop;
    private boolean wholeNumberOnly;

    @NotNull
    public CheckResult check(@NotNull ItemStack stack, double price) {
        if (Double.isInfinite(price) || Double.isNaN(price)) {
            return new CheckResult(Status.NOT_VALID, minPrice, maxPrice);
        }
        if (allowFreeShop) {
            if (price != 0 && price < minPrice) {
                return new CheckResult(Status.REACHED_PRICE_MIN_LIMIT, minPrice, maxPrice);
            }
        }
        if (wholeNumberOnly) {
            try {
                BigDecimal.valueOf(price).setScale(0, RoundingMode.UNNECESSARY);
            } catch (ArithmeticException exception) {
                Util.debugLog(exception.getMessage());
                return new CheckResult(Status.NOT_A_WHOLE_NUMBER, minPrice, maxPrice);
            }
        }
        if (price < minPrice) {
            return new CheckResult(Status.REACHED_PRICE_MIN_LIMIT, minPrice, maxPrice);
        }
        if (maxPrice != -1) {
            if (price > maxPrice) {
                return new CheckResult(Status.REACHED_PRICE_MAX_LIMIT, minPrice, maxPrice);
            }
        }
        double perItemPrice;
        if (QuickShop.getInstance().isAllowStack()) {
            perItemPrice = CalculateUtil.divide(price, stack.getAmount());
        } else {
            perItemPrice = price;
        }
        Map.Entry<Double, Double> materialLimit = Util.getPriceRestriction(stack.getType());
        if (materialLimit != null) {
            if (perItemPrice < materialLimit.getKey() || perItemPrice > materialLimit.getValue()) {
                return new CheckResult(Status.PRICE_RESTRICTED, materialLimit.getKey(), materialLimit.getValue());
            }
            return new CheckResult(Status.PASS, materialLimit.getKey(), materialLimit.getValue());
        }
        return new CheckResult(Status.PASS, minPrice, maxPrice);
    }

    public enum Status {
        PASS,
        REACHED_PRICE_MAX_LIMIT,
        REACHED_PRICE_MIN_LIMIT,
        PRICE_RESTRICTED,
        NOT_A_WHOLE_NUMBER,
        NOT_VALID
    }

    @AllArgsConstructor
    @Data
    public static class CheckResult {
        private PriceLimiter.Status status;
        private double min;
        private double max;
    }
}
