<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

<jsp:include page="/header.jsp" />

	<section>
		<h1><fmt:message key="i18n.adminPage"/></h1>
		<br>
		<table id="tableUsers" class="table_users">
			<tr>
				<th><fmt:message key="i18n.email"/></th>
				<th><fmt:message key="i18n.firstNameEn"/></th>
				<th><fmt:message key="i18n.lastNameEn"/></th>
				<th><fmt:message key="i18n.firstNameUk"/></th>
				<th><fmt:message key="i18n.lastNameUk"/></th>
				<th><fmt:message key="i18n.telNumber"/></th>
				<th><fmt:message key="i18n.serviceType"/></th>
				<th><fmt:message key="i18n.role"/></th>
			</tr>

            <c:forEach var="user" items="${users}">
                <tr class="user_row">
                    <td id="email">${user.email}</td>
                    <td id="firstNameEn">${user.firstNameEn}</td>
                    <td id="lastNameEn">${user.lastNameEn}</td>
                    <td id="firstNameUk">${user.firstNameUk}</td>
                    <td id="lastNameUk">${user.lastNameUk}</td>
                    <td id="telNumber">${user.telNumber}</td>
                    <td id="serviceType">
                        <input id="serviceType" type="hidden" value="${user.serviceType}" />
                        <select id="select" name="${user.email}" onChange="setServiceType(this)">
                            <c:forEach var="serviceType" items="${serviceTypes}">
                                <option id="${serviceType}"
                                        value="${serviceType}"
                                        selected="${serviceType == user.serviceType}">
                                        <fmt:message key="${serviceType.i18n}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </td>
                    <td id="role">
                        <input id="role" type="hidden" value="${user.role}" />
                        <select id="select" name="${user.email}" onChange="setRole(this)">
                            <c:forEach var="aRole" items="${roles}">
                                <option id="${aRole}"
                                        value="${aRole}"
                                        selected="${aRole == user.role}">
                                        <fmt:message key="${aRole.i18n}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
            </c:forEach>

		</table>
    </section>



	<script src="/js/admin.js"></script>

<jsp:include page="/footer.jsp" />