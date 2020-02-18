<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page isErrorPage="true" %>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

<jsp:include page="/WEB-INF/header.jsp" />

	<section>
		<h1>An error occurred</h1>
		<h1 style="color: red; font-size: 64px">${exception.statusCode}</h1>
        <h1 style="color: red"><fmt:message key="${exception.errorMessage}"/></h1>
		<h1>please contact our maintenance service</h1>
    </section>

<jsp:include page="/WEB-INF/footer.jsp" />