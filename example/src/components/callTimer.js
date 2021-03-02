import React, {Component} from 'react';
import {View, Text, StyleSheet, TouchableHighlight} from 'react-native';

import {Stopwatch, Timer} from 'react-native-stopwatch-timer';

export default class CallTimer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      stopwatchStart: this.props.startTimer,
      totalDuration: 90000,
      timerReset: false,
      stopwatchReset: false,
    };
    this.toggleTimer = this.toggleTimer.bind(this);
    this.resetTimer = this.resetTimer.bind(this);
    this.toggleStopwatch = this.toggleStopwatch.bind(this);
    this.resetStopwatch = this.resetStopwatch.bind(this);
  }

  toggleTimer() {
    this.setState({timerStart: !this.state.timerStart, timerReset: false});
  }

  resetTimer() {
    this.setState({timerStart: false, timerReset: true});
  }

  toggleStopwatch() {
    this.setState({
      stopwatchStart: !this.state.stopwatchStart,
      stopwatchReset: false,
    });
  }

  resetStopwatch() {
    this.setState({stopwatchStart: false, stopwatchReset: true});
  }

  getFormattedTime(time) {
    console.log(time);
    this.currentTime = time;
  }

  componentWillUnmount() {
    this.resetTimer();
  }

  render(props) {
    return (
      <Stopwatch
        laps
        start={this.state.stopwatchStart}
        reset={this.state.stopwatchReset}
        options={options}
        getTime={this.getFormattedTime}
      />
    );
  }
}
// const handleTimerComplete = () => alert('custom completion function');

const options = {
  container: {},
  text: {
    fontSize: 30,
    color: 'black',
    marginLeft: 7,
  },
};
