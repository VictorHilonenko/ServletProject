var weekStartsDate;

function init() {
    if($("#table_schedule").length == 0) {
        return;
    }

	$("#date").datepicker();
	$("#date").datepicker("option", $.datepicker.regional[$("html").attr("lang")]);
	
	drawThisWeek();
}

function drawThisWeek() {
	weekStartsDate = startOfCurrentWeek(0);
	drawWeek();
}

function drawPrevWeek() {
	weekStartsDate = startOfCurrentWeek(-1);
	drawWeek();
}

function drawNextWeek() {
	weekStartsDate = startOfCurrentWeek(1);
	drawWeek();
}

//NOTE: js Date.toISOString() returns time in GMT zone, so made this short fix to prevent long games with dates, they are out of scope now
function getISOFormattedLocalDate(date) {
	return date.getFullYear() + '-' + ('0' + (date.getMonth() + 1)).slice(-2) + '-' + ('0' + date.getDate()).slice(-2); 
}

function getISOFormattedFromLocalFormattedDate(date) {
	return date.substring(6,10) + '-' + date.substring(3,5) + '-' + date.substring(0,2); 
}

function getLocalFormattedLocalDate(date) {
	return date.toLocalDate(); 
}

function getShortFormattedLocalDate(date) {
	return ('0' + date.getDate()).slice(-2) + '.' + ('0' + (date.getMonth() + 1)).slice(-2); 
}

//NOTE: in our case first day is monday
//didn't want to use special js libraries to work with dates, so made simple transformation:
function dayOfWeek(date) {
	var day = new Date(date).getDay() - 1;
	if(day == -1) {
		day = 6;
	}
	return day;
}

function startOfCurrentWeek(shift) {
	var res; 
	
	if(shift == 0) {
		var today = Date.now();
		var day = dayOfWeek(today);
		res = today - day*24*3600*1000;
	} else {
		if(weekStartsDate == undefined) {
			weekStartsDate = startOfCurrentWeek(0);
		}
		res = weekStartsDate + shift*7*24*3600*1000;
	}
	
	return res;
}

function cleanSchedulerTable() {
	$(".table_schedule td").each(function(i, td) {
		$(td).html("");
	});		
}

function refreshHeader() {
	startShortDate = getShortFormattedLocalDate(new Date(weekStartsDate));
	endShortDate = getShortFormattedLocalDate(new Date(weekStartsDate + 6*24*3600*1000));

	$("#headerDates").text(startShortDate+" - "+endShortDate);
	
	for (d = 0; d <= 6; d++) {
		$("#d"+d).text(getShortFormattedLocalDate(new Date(weekStartsDate + d*24*3600*1000)));
	}
}

function drawWeek() {
	startDate = getISOFormattedLocalDate(new Date(weekStartsDate));
	endDate = getISOFormattedLocalDate(new Date(weekStartsDate + 6*24*3600*1000));
	
	refreshHeader();
	cleanSchedulerTable();
	
	var apiUrl = "/api/appointments/"+startDate+"/"+endDate;
	$.getJSON(apiUrl, function(data) {
		$.each(data, function(i, appointmentRecord) {
			drawAppointment(appointmentRecord.map);
		});
	}).fail(function (data) {
		showStatus("error", data.responseJSON.error, data.responseJSON.message);
	});
}

function validAppointment(appointmentData) {
	return appointmentData.hasOwnProperty("id") 
		&& appointmentData.hasOwnProperty("date")
		&& appointmentData.hasOwnProperty("time")
		&& appointmentData.hasOwnProperty("serviceType");
}

function cellContent(appointmentData) {
	var elementHTML = "";
	
	if(appointmentData.hasOwnProperty("master_name")) {
		elementHTML += "<strong>Master: </strong>"+appointmentData["master_name"]+"<br>";
	}		
	if(appointmentData.hasOwnProperty("customer_name")) {
		elementHTML += "<strong>Customer: </strong>"+appointmentData["customer_name"]+"<br>";
	}		
	if(appointmentData.hasOwnProperty("serviceType")) {
		elementHTML += "<strong>Service: </strong>"+appointmentData["serviceType"]+"<br>";
	}		
	
	return elementHTML;
}

function drawAppointment(appointmentData) {
	if(!validAppointment(appointmentData)) {
		return;
	}
	
	var appointment_id = appointmentData["id"];
	var date = appointmentData["date"];
	var time = appointmentData["time"];
	
	var idOfCellToBind = "#d"+dayOfWeek(new Date(date))+"_"+time;
	
	var colorClass = " appointment_"+appointmentData["serviceType"].substring(0,1).toLowerCase();
	
	var newElement = $("<a/>", {
	    id: "appointment_"+appointment_id,
	    class: "appointment text_left"+colorClass,
	    href: "/",
		html: cellContent(appointmentData),
	    click: function() {
			openAppointment(appointmentData);
			return false;
		}
	}).appendTo($(idOfCellToBind));
}

function openAppointment(appointmentData) {
	if(appointmentData == null) {
		appointmentData = {
			date: "",
			rights_date: "W",
			time: 12,
			rights_time: "W",
			serviceType: "HAIRDRESSING",
			rights_serviceType: "W"
	    }
	}
	
	applyDataToFields(appointmentData);
	
	showAppointmentDialog();
}

function applyDataToFields(appointmentData) {
	$(".field").each(function(index, field) {
		var visible = false;
		var enabled = false;
		var dataValue = "";
		var ights = "";
		
		if(appointmentData.hasOwnProperty(field.id)) {
			dataValue = appointmentData[field.id];
			
			if(appointmentData.hasOwnProperty("rights_"+field.id)) {
				rights = appointmentData["rights_"+field.id];
				
				if(rights == "W") {
					visible = true;
					enabled = true;
				} else if(rights == "R") {
					visible = true;
				}
			}
		}
		
		setValueToElement(field.id, dataValue);
		setAccesibilityToElement(field.id, visible, enabled);
	});	
}

function setValueToElement(elementId, value) {
	$("#"+elementId).each(function(index, field) {
		if($(this)[0].type == "checkbox") {
			$(this).prop('checked', value == "true");
		} else {
			$(this).val(value);
		}
	});
}

function setAccesibilityToElement(elementId, visible, enabled) {
	$("#div_"+elementId).prop("hidden", !visible);
	$("#"+elementId).prop("disabled", !enabled);
}

function dateAndTimeIsValid(inputDate, inputTime) {
	var allValid = true;
	var locDate;
	
	if(inputDate == "") {
		allValid = false;
	} else {
		locDate = getISOFormattedFromLocalFormattedDate(inputDate);
		var locDateTime = Date.parse(locDate) + (inputTime - new Date(Date.now()).getTimezoneOffset()/60)*3600*1000;
		
		if(locDateTime < Date.now()) {
			allValid = false;
		}
	}
	
	return allValid;
} 

function reserveTime(dlg) {
	var inputDate = $("#date").val();
	var inputTime = $("#time").val();
	
	//TODO proccess all this on server side
	if(!dateAndTimeIsValid(inputDate, inputTime)) {
		showStatus("error", 403, "Specify correct date and time");
		return false;
	}
	
	locDate = getISOFormattedFromLocalFormattedDate(inputDate);
	
	var appointmentDTO = {
		map: {
		    date: locDate,
		    time: inputTime,
			serviceType: $("#serviceType").val()
	    }
    }
	
	$.ajax({
        url: "/api/appointments",
        type: "POST",
        dataType: "json",
        contentType: "application/json",
        success: function (appointmentRecord) {
        	//showStatus("success", 200, "saved");
			dlg.dialog("close");
        	drawWeek();
        },
        error: function (data) {
            showStatus("error", data.responseJSON.error, data.responseJSON.message);
        },
        data: JSON.stringify(appointmentDTO)
    });

	return false;
}

function updateServiceProvided(appointmentId, serviceProvidedNewValue, dlg) {
	var appointmentDTO = {
			map: {
				id: appointmentId,
				serviceProvided: serviceProvidedNewValue
		    }
	    }
	
	$.ajax({
        url: "/api/appointments",
        type: "PUT",
        dataType: "json",
        contentType: "application/json",
        success: function (appointmentDTO) {
        	showStatus("success", 200, "saved");
			dlg.dialog("close");
        	drawWeek();
        },
        error: function (data) {
            showStatus("error", data.responseJSON.error, data.responseJSON.message);
			dlg.dialog("close");
        	drawWeek();
        },
        data: JSON.stringify(appointmentDTO)
    });
}

function showAppointmentDialog() {
	var setButtons = {};
	
	if($("input#id").val() == "") {
		setButtons["Reserve time"] = function() {
			reserveTime($(this));
		};
	}
	
	var serviceProvidedVisible = !$("#div_serviceProvided")[0].hidden;
	var serviceProvidedEnabled = !$("input#serviceProvided")[0].disabled;
	var serviceProvidedCurrentValue = $("input#serviceProvided").prop("checked");
	
	if(serviceProvidedVisible&&serviceProvidedEnabled) {
		setButtons["Save"] = function() {
			var serviceProvidedNewValue = $("input#serviceProvided").prop("checked");
			
			if(serviceProvidedNewValue != serviceProvidedCurrentValue) {
				updateServiceProvided($("input#id").val(), serviceProvidedNewValue, $(this));
			}
		};
	}
	
	var btnCloseTitle = "Close";
	if(Object.keys(setButtons).length > 0) {
		btnCloseTitle = "Cancel";
	}
	setButtons[btnCloseTitle] = function() {
		$(this).dialog("close");
	};
	
	$(function() {
		$("#dialog-appointment").dialog({
			resizable: false,
			height: "auto",
			width: 600,
			modal: true,
			buttons: setButtons
		});
	});
}

function suggestRegistration() {
	$(function() {
		$("#dialog-login").dialog({
			resizable: false,
			height: "auto",
			width: 400,
			modal: true,
			buttons: {
				"Log in": function() {
					$(location).attr('href', '/login');
				},
				"Register": function() {
					$(location).attr('href', '/registration');
				},
				Cancel: function() {
					$(this).dialog("close");
				}
			}
		});
	});
}