import React, {Component, useState, useEffect} from 'react';
import {useStopwatch} from 'react-timer-hook';
import {View, Text, StyleSheet, TouchableOpacity} from 'react-native';
import {responsiveFonts, responsiveHeight, responsiveWidth} from '@resources';

function MyStopwatch(props) {
  const {
    seconds,
    minutes,
    hours,
    days,
    isRunning,
    start,
    pause,
    reset,
  } = useStopwatch({autoStart: props.start});

  useEffect(() => {
    if (props.paused) {
      pause();
    }
  }, [props.paused]);

  return (
    <View style={{textAlign: 'center'}}>
      <Text style={{fontSize: responsiveFonts(18)}}>
        {hours} : {minutes} : {seconds}
      </Text>
    </View>
  );
}

export default class MyStopwatch2 extends Component {
  render() {
    return <MyStopwatch start={this.props.start} paused={this.props.pause} />;
  }
}
