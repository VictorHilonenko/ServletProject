<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

<jsp:include page="/header.jsp" />

	<section>
		<h1>Login page</h1>
		<div class="form-cont">
			<form action="/login" method="post">
                <c:if test = "${errors.containsKey('')}">
                    <h1><div class="text_alert"><strong><fmt:message key="${errors.get('')}"/></strong></div></h1>
                </c:if>

				<div>
					<label for="email"><strong><fmt:message key="i18n.email"/></strong></label>:
					<br>
                    <input type="text" name="email" class="form-control" autofocus="autofocus"
                        placeholder="<fmt:message key="i18n.email"/>"
                        value="<c:if test="${form != null}">${form.email}</c:if>" />

                    <c:if test = "${errors.containsKey('email')}">
                        <div class="text_alert"><fmt:message key="${errors.get('email')}"/></div>
                    </c:if>
				</div>

				<div>
					<label for="password"><strong><fmt:message key="i18n.password"/></strong></label>:
					<br>
                    <input type="password" name="password" class="form-control" autofocus="autofocus"
                        placeholder="<fmt:message key="i18n.password"/>"
                        value="<c:if test="${form != null}">${form.password}</c:if>" />

                    <c:if test = "${errors.containsKey('password')}">
                        <div class="text_alert"><fmt:message key="${errors.get('password')}"/></div>
                    </c:if>
				</div>

				<input type="submit" class="send" value="<fmt:message key="i18n.login"/>" />
			</form>
		</div>
    </section>

<jsp:include page="/footer.jsp" />