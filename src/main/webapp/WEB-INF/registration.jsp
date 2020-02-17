<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

<jsp:include page="/WEB-INF/header.jsp" />

	<section>
		<h1><fmt:message key="i18n.registrationPage"/></h1>
		<div class="form-cont">
            <form action="/registration" method="post">

				<div>
					<label for="firstNameEn"><strong><fmt:message key="i18n.firstNameEn"/></strong></label>:
					<br>
                    <input type="text" name="firstNameEn" class="form-control" autofocus="autofocus"
                        placeholder="<fmt:message key="i18n.firstNameEn"/>"
                        value="<c:if test="${form != null}">${form.firstNameEn}</c:if>" />

                    <c:if test = "${errors.containsKey('firstNameEn')}">
                        <div class="text_alert"><fmt:message key="${errors.get('firstNameEn')}"/></div>
                    </c:if>
				</div>
				<div>
					<label for="lastNameEn"><strong><fmt:message key="i18n.lastNameEn"/></strong></label>:
					<br>
                    <input type="text" name="lastNameEn" class="form-control" autofocus="autofocus"
                        placeholder="<fmt:message key="i18n.lastNameEn"/>"
                        value="<c:if test="${form != null}">${form.lastNameEn}</c:if>" />

                    <c:if test = "${errors.containsKey('lastNameEn')}">
                        <div class="text_alert"><fmt:message key="${errors.get('lastNameEn')}"/></div>
                    </c:if>
				</div>

				<div>
					<label for="firstNameUk"><strong><fmt:message key="i18n.firstNameUk"/></strong></label>:
					<br>
                    <input type="text" name="firstNameUk" class="form-control" autofocus="autofocus"
                        placeholder="<fmt:message key="i18n.firstNameUk"/>"
                        value="<c:if test="${form != null}">${form.firstNameUk}</c:if>" />

                    <c:if test = "${errors.containsKey('firstNameUk')}">
                        <div class="text_alert"><fmt:message key="${errors.get('firstNameUk')}"/></div>
                    </c:if>
				</div>
				<div>
					<label for="lastNameUk"><strong><fmt:message key="i18n.lastNameUk"/></strong></label>:
					<br>
                    <input type="text" name="lastNameUk" class="form-control" autofocus="autofocus"
                        placeholder="<fmt:message key="i18n.lastNameUk"/>"
                        value="<c:if test="${form != null}">${form.lastNameUk}</c:if>" />

                    <c:if test = "${errors.containsKey('lastNameUk')}">
                        <div class="text_alert"><fmt:message key="${errors.get('lastNameUk')}"/></div>
                    </c:if>
				</div>

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
					<label for="telNumber"><strong><fmt:message key="i18n.telNumber"/></strong></label>:
					<br>
                    <input type="text" name="telNumber" class="form-control" autofocus="autofocus"
                        placeholder="<fmt:message key="i18n.telNumber"/>"
                        value="<c:if test="${form != null}">${form.telNumber}</c:if>" />

                    <c:if test = "${errors.containsKey('telNumber')}">
                        <div class="text_alert"><fmt:message key="${errors.get('telNumber')}"/></div>
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
				<input type="submit" class="send" value="<fmt:message key="i18n.register"/>" />
			</form>
		</div>
	</section>

<jsp:include page="/WEB-INF/footer.jsp" />