/**
 *	iComfort Thermostat SmartDevice
 *
 *	Author: Dario Rossi
 *	Date: 2015-03-27
 *  Date: 2015-05-12: Fixed bug in temperature display that was preventing from displaying correctly in iOS App.  Removed decimals and degree symbol from color ranges 	
 *
 ***************************
 *
 *  Copyright 2015 Dario Rossi
 *
 *  Original Code obtained from Jason Mok located here:
 *  https://github.com/copy-ninja/SmartThings_iComfort
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 **************************
 *
 * REQUIREMENTS:
 * Refer to iComfort Connection Manager SmartApp
 *
 **************************
 * 
 * USAGE:
 * Put this in Device Type. Don't install until you have all other device types scripts added
 * Refer to iComfort Connection Manager SmartApp
 *
 */
 metadata {
	definition (name: "iComfort Thermostat", namespace: "dario.rossi", author: "Dario Rossi") {
		capability "Thermostat"
		capability "Relative Humidity Measurement"
		capability "Polling"
		capability "Refresh"
	        
		attribute "temperatureDisplay", "string"
		attribute "heatingSetpointDisplay", "string"
		attribute "coolingSetpointDisplay", "string"
		attribute "thermostatProgram", "string"
	        
		command "heatLevelUp"
		command "heatLevelDown"
		command "coolLevelUp"
		command "coolLevelDown"
		command "switchMode"
		command "switchFanMode"
		command "switchProgram"
		command "setThermostatProgram"
	}

	simulator { }

	tiles {
		/** valueTile("temperature", "device.temperature", width: 2, height: 2) { **/
        valueTile("temperature", "device.temperature") {
			state("temperature", label:'${currentValue}', unit: "C")
		}
		valueTile("temperatureDisplay", "device.temperatureDisplay", width: 2, height: 2) {
			state("temperature", label:'${currentValue}', 
				backgroundColors:[
					[value: 31, unit: "F", color: "#153591"],
					[value: 44, unit: "F", color: "#1e9cbb"],
					[value: 59, unit: "F", color: "#90d2a7"],
					[value: 74, unit: "F", color: "#44b621"],
					[value: 84, unit: "F", color: "#f1d801"],
					[value: 95, unit: "F", color: "#d04e00"],
					[value: 96, unit: "F", color: "#bc2323"],
					[value: 0, unit: "C", color: "#153591"],
					[value: 6, unit: "C", color: "#1e9cbb"],
					[value: 15, unit: "C", color: "#90d2a7"],
					[value: 23, unit: "C", color: "#44b621"],
					[value: 29, unit: "C", color: "#f1d801"],
					[value: 35, unit: "C", color: "#d04e00"],
					[value: 37, unit: "C", color: "#bc2323"]
				] 
			)
		}
        
		/** valueTile("humidity", "device.humidity", inactiveLabel: false) **/
        
        valueTile("humidity", "device.humidity") {
			state("humidity", label:'${currentValue}% \nHumidity' , unit: "Humidity",
				backgroundColors:[
					[value: 20, unit: "Humidity", color: "#b2d3f9"],
					[value: 30, unit: "Humidity", color: "#99c5f8"],
					[value: 35, unit: "Humidity", color: "#7fb6f6"],
					[value: 40, unit: "Humidity", color: "#66a8f4"],
					[value: 45, unit: "Humidity", color: "#4c99f3"],
					[value: 50, unit: "Humidity", color: "#328bf1"],
					[value: 55, unit: "Humidity", color: "#197cef"],
					[value: 60, unit: "Humidity", color: "#006eee"],
					[value: 70, unit: "Humidity", color: "#0063d6"],
				]
			)
		}
		standardTile("thermostatOperatingState", "device.thermostatOperatingState", canChangeIcon: false, decoration: "flat") {
			state("idle",            icon: "st.thermostat.ac.air-conditioning", label: "Idle")
			state("waiting",         icon: "st.thermostat.ac.air-conditioning", label: "Waiting")
			state("heating",         icon: "st.thermostat.heating")
			state("cooling",         icon: "st.thermostat.cooling")
			state("emergency heat",  icon: "st.thermostat.emergency-heat")
			
		}
		standardTile("thermostatMode", "device.thermostatMode", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
			state("auto",		action:"switchMode",    nextState: "auto",		icon: "st.thermostat.auto")
			state("heat",		action:"switchMode",    nextState: "heat",		icon: "st.thermostat.heat")
			state("cool",		action:"switchMode",    nextState: "cool",		icon: "st.thermostat.cool")
			state("off",		action:"switchMode",    nextState: "off",		icon: "st.thermostat.heating-cooling-off")
			state("emergency heat",	action:"switchMode",    nextState: "emergency heat",	icon: "st.thermostat.emergency-heat")
			state("program",	action:"switchMode",    nextState: "program",		icon: "st.thermostat.ac.air-conditioning", label: "Program")
		}
		standardTile("thermostatFanMode", "device.thermostatFanMode", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
			state("auto",      action:"switchFanMode", nextState: "auto",       icon: "st.thermostat.fan-auto")
			state("on",        action:"switchFanMode", nextState: "on",         icon: "st.thermostat.fan-on")
			state("circulate", action:"switchFanMode", nextState: "circulate",  icon: "st.thermostat.fan-circulate")
			state("off",       action:"switchFanMode", nextState: "off",        icon: "st.thermostat.fan-off")
		}
		standardTile("heatLevelUp", "device.switch", canChangeIcon: false, inactiveLabel: true, decoration: "flat" ) {
			state("heatLevelUp",   action:"heatLevelUp",   icon:"st.thermostat.thermostat-up", backgroundColor:"#F7C4BA")
		}        
		valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel: false) {
			state("heat", label:'${currentValue}°')
		}
		valueTile("heatingSetpointDisplay", "device.heatingSetpointDisplay", inactiveLabel: false) {
			state("heat", label:'${currentValue}', 
				backgroundColors:[
					[value: "40", unit: "F", color: "#f49b88"],
					[value: "50", unit: "F", color: "#f28770"],
					[value: "60", unit: "F", color: "#f07358"],
					[value: "70", unit: "F", color: "#ee5f40"],
					[value: "80", unit: "F", color: "#ec4b28"],
					[value: "90", unit: "F", color: "#ea3811"],
					[value: "5",  unit: "C", color: "#f49b88"],
					[value: "10", unit: "C", color: "#f28770"],
					[value: "15", unit: "C", color: "#f07358"],
					[value: "20", unit: "C", color: "#ee5f40"],
					[value: "25", unit: "C", color: "#ec4b28"],
					[value: "30", unit: "C", color: "#ea3811"]
				]
			)
		}
		standardTile("heatLevelDown", "device.switch", canChangeIcon: false, inactiveLabel: true, decoration: "flat") {
			state("heatLevelDown", action:"heatLevelDown", icon:"st.thermostat.thermostat-down", backgroundColor:"#F7C4BA")
		}        
		standardTile("coolLevelUp", "device.switch", canChangeIcon: false, inactiveLabel: true, decoration: "flat" ) {
			state("coolLevelUp",   action:"coolLevelUp",   icon:"st.thermostat.thermostat-up" , backgroundColor:"#BAEDF7")
		}
		valueTile("coolingSetpoint", "device.coolingSetpoint", inactiveLabel: false) {
			state("cool", label:'${currentValue}°')
		}
		valueTile("coolingSetpointDisplay", "device.coolingSetpointDisplay", inactiveLabel: false) {
			state("cool", label:'${currentValue}', 
				backgroundColors:[
					[value: "40", unit: "F", color: "#88e1f4"],
					[value: "50", unit: "F", color: "#70dbf2"],
					[value: "60", unit: "F", color: "#58d5f0"],
					[value: "70", unit: "F", color: "#40cfee"],
					[value: "80", unit: "F", color: "#28c9ec"],
					[value: "90", unit: "F", color: "#11c3ea"],
					[value: "5", unit: "C",  color: "#88e1f4"],
					[value: "10", unit: "C",  color: "#70dbf2"],
					[value: "15", unit: "C",  color: "#58d5f0"],
					[value: "20", unit: "C",  color: "#40cfee"],
					[value: "25", unit: "C",  color: "#28c9ec"],
					[value: "30", unit: "C",  color: "#11c3ea"]
				]
			)
		}
		standardTile("coolLevelDown", "device.switch", canChangeIcon: false, inactiveLabel: true, decoration: "flat") {
			state("coolLevelDown", action:"coolLevelDown", icon:"st.thermostat.thermostat-down", backgroundColor:"#BAEDF7")
		}
		valueTile("thermostatProgram", "device.thermostatProgram", inactiveLabel: false, decoration: "flat") {
			state("program", action:"switchProgram", label: '${currentValue}')
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state("default", action:"refresh.refresh",        icon:"st.secondary.refresh")
		}
		main "temperatureDisplay"
		details(["temperatureDisplay", "humidity", "thermostatOperatingState",  "heatLevelUp", "coolLevelUp", "thermostatFanMode", "heatingSetpointDisplay", "coolingSetpointDisplay", "thermostatMode", "heatLevelDown", "coolLevelDown", "thermostatProgram", "refresh" ])
	}
}

def installed() { initialize() }

def updated() { initialize() }

def initialize() {
	state.polling = [ 
		last: 0,
		runNow: true
	]	
	refresh() 
}

def parse(String description) { }

def refresh() { 
	state.polling.runNow = true
	parent.refresh() 
}

def poll() { updateThermostatData(parent.getDeviceStatus(this.device)) 
	if (parent.debugVerbosityLevel.toInteger() > 5) {
    	log.debug "poll.This Device: " + this.device
        log.debug "poll.This Device Network ID: " + this.deviceNetworkId
        log.debug "poll.parent.getDeviceStatus(this.device): " + parent.getDeviceStatus(this.device)
    }


}

def updateThermostatData(thermostatData) {
	//update the state data
	def next = (state.polling.last?:0) + 15000  //will not update if it's not more than 15 seconds. this is to stop polling from updating all the state data.
		if (parent.debugVerbosityLevel.toInteger() > 5) {
    		log.debug "Now:next: " + now().toString() + ":" + next.toString()
            
    	}

	if ((now() > next) || (state.polling.runNow)) {
		if (parent.debugVerbosityLevel.toInteger() > 5) {
    		log.debug "update.This Device: " + this.device
            log.debug "update.parent.getDeviceStatus(this.device): " + parent.getDeviceStatus(this.device)
    	}

		state.polling.last = now()
		state.polling.runNow = false
		
		def thermostatProgramSelection
		def thermostatProgramMode = (device.currentValue("thermostatProgram") == "Manual")?"0":"1"
		def thermostatMode = (device.currentState("thermostatMode")?.value)?device.currentState("thermostatMode")?.value:"auto"
		
		thermostatData.each { name, value -> 
			if (name == "temperature" || name == "coolingSetpoint" || name == "heatingSetpoint") {
				def displayValue = value.toString()
				if (parent.getTemperatureUnit() == "C") {
					displayValue  = String.format("%.1f", (Math.round(value * 20) / 20)) + "°"
				} else {
					displayValue  = String.format("%.0f", value) + "°"
				}
				def displayName = name + "Display"
				sendEvent(name: displayName, value: displayValue as String, unit: parent.getTemperatureUnit(), displayed: false)
				sendEvent(name: name, value: value , unit: parent.getTemperatureUnit())
				if (parent.debugVerbosityLevel.toInteger() > 5) {
					log.debug "Sending Event: " + [name, value, parent.getTemperatureUnit()]
				}
			} else if (name == "thermostatProgramMode") {
				thermostatProgramMode = value
			} else if (name == "thermostatProgramSelection") {
				thermostatProgramSelection = value
			} else if (name == "thermostatMode") {
				thermostatMode = value
			} else {
				sendEvent(name: name, value: value, displayed: false)
				if (parent.debugVerbosityLevel.toInteger() > 5) {
					log.debug "Sending Misc Event: " + [name, value]
				}
			}
		}
        		
		if (thermostatProgramMode == "0") {
			sendEvent(name: "thermostatMode", value: thermostatMode)
			sendEvent(name: "thermostatProgram", value: "Manual")
			if (parent.debugVerbosityLevel.toInteger() > 5) {
				log.debug "Sending Event: " + ["thermostatMode", thermostatMode]
				log.debug "Sending Event: " + ["thermostatProgram", "Manual"]			
			}
		} else {
			sendEvent(name: "thermostatMode", value: "program")
			if (parent.debugVerbosityLevel.toInteger() > 5) {
				log.debug "Sending Event: " + ["thermostatMode", "program"]
			}
			if (thermostatProgramSelection) {
				sendEvent(name: "thermostatProgram", value: parent.getThermostatProgramName(this.device, thermostatProgramSelection))
				if (parent.debugVerbosityLevel.toInteger() > 5) {
					log.debug "Sending Event: " + ["thermostatProgram", parent.getThermostatProgramName(this.device, thermostatProgramSelection)]
				}
			}
		}
	}
}

def setHeatingSetpoint(Number heatingSetpoint) {
	// define maximum & minimum for heating setpoint 
	def minHeat = parent.getSetPointLimit(this.device, "heatingSetPointLow")
	def maxHeat = parent.getSetPointLimit(this.device, "heatingSetPointHigh")
	def diffHeat = parent.getSetPointLimit(this.device, "differenceSetPoint").toInteger()
	
	heatingSetpoint = (heatingSetpoint < minHeat)? minHeat : heatingSetpoint
	heatingSetpoint = (heatingSetpoint > maxHeat)? maxHeat : heatingSetpoint

	// check cooling setpoint 
	def heatSetpointDiff = parent.getTemperatureNext(heatingSetpoint, diffHeat)
	def coolingSetpoint = device.currentValue("coolingSetpoint")
	coolingSetpoint = (heatSetpointDiff > coolingSetpoint)? heatSetpointDiff : coolingSetpoint
    
	setThermostatData([coolingSetpoint: coolingSetpoint, heatingSetpoint: heatingSetpoint])
}

def setCoolingSetpoint(Number coolingSetpoint) { 
	// define maximum & minimum for cooling setpoint 
	def minCool = parent.getSetPointLimit(this.device, "coolingSetPointLow")
	def maxCool = parent.getSetPointLimit(this.device, "coolingSetPointHigh")
	def diffHeat = parent.getSetPointLimit(this.device, "differenceSetPoint").toInteger()

	coolingSetpoint = (coolingSetpoint < minCool)? minCool : coolingSetpoint
	coolingSetpoint = (coolingSetpoint > maxCool)? maxCool : coolingSetpoint
	
	// check heating setpoint 
	def coolSetpointDiff = parent.getTemperatureNext(coolingSetpoint, (diffHeat * -1))
    if (parent.debugVerbosityLevel.toInteger() > 5) {
    	log.debug "coolSetpointDiff : " + coolSetpointDiff
	}
	def heatingSetpoint = device.currentValue("heatingSetpoint")
	heatingSetpoint = (coolSetpointDiff < heatingSetpoint)? coolSetpointDiff : heatingSetpoint
	    
	setThermostatData([coolingSetpoint: coolingSetpoint, heatingSetpoint: heatingSetpoint])
}

def switchMode() {
	def currentMode = device.currentState("thermostatMode")?.value
	if (parent.debugVerbosityLevel.toInteger() > 5) {
		log.debug "currentMode: " + currentMode
	}
	switch (currentMode) {
		case "off":
			setThermostatMode("heat")
			break
		case "heat":
			setThermostatMode("cool")
			break
		case "cool":
			setThermostatMode("auto")
			break
		case "auto":
			setThermostatMode("off")
			break
		case "program":
			setThermostatMode("auto")
			break
		default:
			setThermostatMode("auto")
	}
	if(!currentMode) { setThermostatMode("auto") }
}

def off() { setThermostatMode("off") }
def heat() { setThermostatMode("heat") }
def emergencyHeat() { setThermostatMode("emergency heat") }
def cool() { setThermostatMode("cool") }
def auto() { setThermostatMode("auto") }

def switchFanMode() {
	def currentFanMode = device.currentState("thermostatFanMode")?.value
	switch (currentFanMode) {
		case "auto":
			setThermostatFanMode("on")
			break
		case "on":
			setThermostatFanMode("circulate")
			break
		case "circulate":
			setThermostatFanMode("auto")
			break
		default:
			setThermostatFanMode("auto")
	}
	if(!currentFanMode) { setThermostatFanMode("auto") }
}

def setThermostatMode(mode) { 
	def thermostatProgramMode = (device.currentValue("thermostatProgram") == "Manual")?"0":"1"
	if (thermostatProgramMode != "0") {
		parent.setProgram(this.device, "0", state.thermostatProgramSelection)
		setThermostatData([ thermostatProgramMode: "0", thermostatMode: mode ])
	} else {
		setThermostatData([ thermostatMode: mode ])
	}
}

def fanOn() { setThermostatFanMode("on") }
def fanAuto() { setThermostatFanMode("auto") }
def fanCirculate() { setThermostatFanMode("circulate") }
def setThermostatFanMode(fanMode) { setThermostatData([ thermostatFanMode: fanMode ]) }
def heatLevelUp() {	
	def heatingSetpoint = device.currentValue("heatingSetpoint")
	setHeatingSetpoint(parent.getTemperatureNext(heatingSetpoint, 1))   
}
def heatLevelDown() { 
	def heatingSetpoint = device.currentValue("heatingSetpoint")
	setHeatingSetpoint(parent.getTemperatureNext(heatingSetpoint, -1))  
}
def coolLevelUp() { 
	def coolingSetpoint = device.currentValue("coolingSetpoint")
	setCoolingSetpoint(parent.getTemperatureNext(coolingSetpoint, 1))  
}
def coolLevelDown() { 
	def coolingSetpoint = device.currentValue("coolingSetpoint")
	setCoolingSetpoint(parent.getTemperatureNext(coolingSetpoint, -1))  
}

def switchProgram() {
	def currentProgram = device.currentValue("thermostatProgram")
	def nextProgramID = parent.getThermostatProgramNext(this.device, currentProgram)
	setThermostatProgram(nextProgramID)
}

def setThermostatProgram(programID) {
	state.polling.runNow = true
	updateThermostatData([thermostatProgramMode: "1", thermostatProgramSelection: programID])
	def thermostatResult = parent.setProgram(this.device, "1", programID)
	state.polling.runNow = true
	updateThermostatData(thermostatResult)
}

def setThermostatData(thermostatData) {
	state.polling.runNow = true
	updateThermostatData(thermostatData)
	def thermostatResult = parent.setThermostat(this.device, thermostatData)
	state.polling.runNow = true
	updateThermostatData(thermostatResult)
}