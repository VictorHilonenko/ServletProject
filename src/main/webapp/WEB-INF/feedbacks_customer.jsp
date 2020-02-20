<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

<jsp:include page="/WEB-INF/header.jsp" />

	<section>
		<c:if test = "${listFeedbacksDTOToLeave != null && !listFeedbacksDTOToLeave.isEmpty()}">
			<h1><fmt:message key="i18n.leaveYourFeedbacksPlease"/></h1>
			<div class="schedule_cont">
                <c:if test = "${errors.containsKey('')}">
                    <h1><div class="text_alert"><strong><fmt:message key="${errors.get('')}"/></strong></div></h1>
                </c:if>
                <c:forEach var="feedbackDTO" items="${listFeedbacksDTOToLeave}">
					<form method="POST">
						<div class="form-block">
							<span class="popup_label"><fmt:message key="i18n.date"/>:</span>
							<span>${feedbackDTO.appointmentDate}</span>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<span class="popup_label"><fmt:message key="i18n.time"/>:</span>
							<span>${feedbackDTO.appointmentTime}:00</span>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<span class="popup_label"><fmt:message key="i18n.serviceType"/>:</span>
							<span><fmt:message key="${feedbackDTO.serviceType}"/></span>
						</div>

						<div class="form-block">
							<span class="popup_label"><fmt:message key="i18n.USER"/>:</span>
							<span>${feedbackDTO.customerName}</span>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<span class="popup_label"><fmt:message key="i18n.MASTER"/>:</span>
							<span>${feedbackDTO.masterName}</span>
						</div>

						<div class="form-block">
							<span class="popup_label"><fmt:message key="i18n.howDoYouRate"/>:</span>
							<br>
                            <c:if test = "${form.id == feedbackDTO.id && errors.containsKey('rating')}">
                                <div class="text_alert"><fmt:message key="${errors.get('rating')}"/></div>
                            </c:if>
							<div class="flex-container field">
                                <c:forEach var="i" begin="1" end="10" step="1" >
                                    <input type="radio" name="rating" class="radio-button" value="${i}" <c:if test = "${form.id == feedbackDTO.id}"><c:if test = "${form.rating == i}">checked</c:if></c:if> >${i}
                                </c:forEach>
							</div>
							<br>
							<span class="popup_label"><fmt:message key="i18n.yourTextHere"/>:</span>
							<br>
							<textarea name="textMessage" class="popup_text field"><c:if test = "${form.id == feedbackDTO.id}">${form.textMessage}</c:if></textarea>
                            <c:if test = "${form.id == feedbackDTO.id && errors.containsKey('textMessage')}">
                                <div class="text_alert"><fmt:message key="${errors.get('textMessage')}"/></div>
                            </c:if>
						</div>

						<input type="hidden" name="id" class="popup_input field" value="${feedbackDTO.id}" />
						<button type="submit" class="ui-button ui-corner-all ui-widget"><fmt:message key="i18n.leaveFeedback"/></button>
					</form>
				</c:forEach>
			</div>
		</c:if>

		<c:if test = "${paginationFeedbacksDTO != null}">
		    <c:if test = "${paginationFeedbacksDTO.totalRecords > 0}">
                <div class="schedule_cont">
                    <h1><fmt:message key="i18n.yourFeedbacks"/></h1>
                    <table class="table_schedule">
                        <tr>
                            <th><fmt:message key="i18n.date"/></th>
                            <th><fmt:message key="i18n.time"/></th>
                            <th><fmt:message key="i18n.serviceType"/></th>
                            <th><fmt:message key="i18n.USER"/></th>
                            <th><fmt:message key="i18n.MASTER"/></th>
                            <th><fmt:message key="i18n.rating"/></th>
                            <th><fmt:message key="i18n.text"/></th>
                        </tr>
                        <c:forEach var="feedbackDTO" items="${paginationFeedbacksDTO.items}">
                            <tr>
                                <td>${feedbackDTO.appointmentDate}</td>
                                <td>${feedbackDTO.appointmentTime}</td>
                                <td><fmt:message key="${feedbackDTO.serviceType}"/></td>
                                <td>${feedbackDTO.customerName}</td>
                                <td>${feedbackDTO.masterName}</td>
                                <td>${feedbackDTO.rating}</td>
                                <td>${feedbackDTO.textMessage}</td>
                            </tr>
        				</c:forEach>
                    </table>

                    <c:if test = "${paginationFeedbacksDTO.totalPages > 1}">
                        <div class="pagination">
                            <c:if test = "${paginationFeedbacksDTO.hasPrevious()}">
                                <a href="/feedbacks?page=${paginationFeedbacksDTO.page-1}">&laquo;</a>
                            </c:if>

                            <c:forEach var="i" begin="0" end="${paginationFeedbacksDTO.totalPages - 1}" step="1">
                                <c:choose>
                                    <c:when test = "${paginationFeedbacksDTO.page == i}">
                                        <a class="active" >${i+1}</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="/feedbacks?page=${i}">${i+1}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:if test = "${paginationFeedbacksDTO.hasNext()}">
                                <a href="/feedbacks?page=${paginationFeedbacksDTO.page+1}">&raquo;</a>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </c:if>
		    <c:if test = "${paginationFeedbacksDTO.totalRecords == 0}">
                <div class="schedule_cont">
                    <h3><fmt:message key="i18n.nothing_to_show"/></h3>
                </div>
            </c:if>
		</c:if>

    </section>
    <br>
    <br>
    <br>

<jsp:include page="/WEB-INF/footer.jsp" />