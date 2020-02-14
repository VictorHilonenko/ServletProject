<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

<jsp:include page="/header.jsp" />

	<section>
		<h1 style="color: green"><fmt:message key="i18n.congrats"/></h1>
		<h1 style="color: green"><fmt:message key="i18n.nowLogin"/></h1>
		<h1><a href="/login"><input type="submit" class="send" value="<fmt:message key="i18n.login"/>" /></a></h1>
	</section>

<jsp:include page="/footer.jsp" />