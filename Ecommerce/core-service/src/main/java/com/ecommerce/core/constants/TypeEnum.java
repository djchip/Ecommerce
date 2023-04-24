package com.ecommerce.core.constants;

import java.util.Objects;

public enum TypeEnum {
    LABEL("LABEL", "Khi file Excel up lên là biểu mẫu( đánh dấu dữ liệu được lưu là tên của cột trong file Excel)"),
    DATA("DATA", "Khi file Excel up lên là cơ sở dữ liệu( đánh dấu dữ liệu được lưu là giá trị của cột trong file Excel)."),
    COUNT("COUNT", "Khi row là dữ liệu được đếm theo số lượng bản ghi."),
    SUM("SUM", "Khi row là dữ liệu tổng giá trị các bản ghi"),
    AVG("AVG", "Khi row là dữ liệu trung bình giá trị các bản ghi");

    public String value;
    public String description;

    private TypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static TypeEnum getByValue(final String value) {
        for (TypeEnum e : values()) {
            if (Objects.equals(e.getValue(), value)) {
                return e;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
