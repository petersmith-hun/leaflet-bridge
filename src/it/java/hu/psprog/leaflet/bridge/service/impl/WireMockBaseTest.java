package hu.psprog.leaflet.bridge.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import hu.psprog.leaflet.api.rest.response.common.BaseBodyDataModel;
import hu.psprog.leaflet.api.rest.response.common.PaginationDataModel;
import hu.psprog.leaflet.api.rest.response.common.SEODataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import org.junit.ClassRule;
import org.junit.Rule;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Wiremock configuration base class for service tests.
 *
 * @author Peter Smith
 */
public abstract class WireMockBaseTest {

    static final String AUTHORIZATION_HEADER = "Authorization";
    static final StringValuePattern VALUE_PATTERN_BEARER_TOKEN = equalTo("Bearer token");
    static final String LIMIT = "limit";
    static final String ORDER_BY = "orderBy";
    static final String ORDER_DIRECTION = "orderDirection";

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(options().port(9999));

    @Rule
    public WireMockClassRule wireMockInstanceRule = wireMockRule;

    String prepareURI(String template, Object ... values) {

        String preparedString = template;
        for (Object value : values) {
            preparedString = preparedString.replaceFirst("(\\{[a-zA-Z]+})", String.valueOf(value));
        }

        return preparedString;
    }

    <T extends BaseBodyDataModel> WrapperBodyDataModel<T> prepareWrappedListDataModel(T body) {
        return WrapperBodyDataModel.getBuilder()
                .withBody(body)
                .withSeo(SEODataModel.getBuilder()
                        .withMetaDescription("SEO Desc")
                        .withMetaKeywords("SEO Keywords")
                        .withMetaTitle("SEO Title")
                        .withPageTitle("Page title")
                        .build())
                .withPagination(PaginationDataModel.getBuilder()
                        .withEntityCount(10)
                        .withEntityCountOnPage(5)
                        .withHasNext(true)
                        .withHasPrevious(false)
                        .withFirst(true)
                        .withLast(false)
                        .withPageCount(2)
                        .withPageNumber(1)
                        .build())
                .build();
    }
}
