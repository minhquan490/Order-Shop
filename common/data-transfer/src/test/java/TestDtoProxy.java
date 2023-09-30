import com.bachlinh.order.core.annotation.DtoProxy;
import com.bachlinh.order.dto.proxy.Proxy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@DtoProxy
public class TestDtoProxy extends TestDto implements Proxy<TestDto, TestEntity> {

    @JsonIgnore
    private TestEntity delegate;

    @Override
    @JsonProperty("test_field_1")
    public String getTestField1() {
        return this.delegate.getTestField1();
    }

    @Override
    @JsonProperty("test_field_2")
    public String getTestField2() {
        return this.delegate.getTestField2();
    }

    @Override
    @JsonIgnore
    public TestDto wrap(TestEntity source) {
        this.delegate = source;
        return this;
    }

    @Override
    public Class<TestDto> proxyForType() {
        return TestDto.class;
    }

    @Override
    public Proxy<?, ?> getInstance() {
        return new TestDtoProxy();
    }
}
