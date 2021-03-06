package pl.training.jpa.converters;

import org.javamoney.moneta.FastMoney;
import pl.training.jpa.common.LocalMoney;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

//@Converter(autoApply = true)
public class FastMoneyConverter implements AttributeConverter<FastMoney, String> {

    @Override
    public String convertToDatabaseColumn(FastMoney fastMoney) {
        return fastMoney != null ? fastMoney.toString() : null;
    }

    @Override
    public FastMoney convertToEntityAttribute(String value) {
        return value != null ? FastMoney.parse(value) : LocalMoney.zero();
    }

}
