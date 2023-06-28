import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@Dto(forType = "TestEntity")
public class TestDto {

    @MappedDtoField(targetField = "testField1", outputJsonField = "test_field_1")
    private String testField1;

    @MappedDtoField(targetField = "testField2", outputJsonField = "test_field_2")
    private String testField2;

    public String getTestField1() {
        return testField1;
    }

    public String getTestField2() {
        return testField2;
    }
}
