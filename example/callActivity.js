import {AppRegistry} from 'react-native';
import {name as appName} from './app.json';

import React, {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  DeviceEventEmitter,
} from 'react-native';
import {CustomButton, image} from '@src';
import ReplaceDialer from 'react-native-replace-dialer';

import CallState from 'react-native-call-state';

export default class CallActivity extends Component {
  constructor(props) {
    super(props);
    this.state = {
      connected: false,
      callType: '',
      phoneNumber: '',
      bluetoothName: 'Bluetooth',
    };
    this.startListenerTapped();
    ReplaceDialer.getBluetoothName((name) => {
      this.setState({bluetoothName: name});
    });
  }

  // call state start
  startListenerTapped() {
    CallState.startListener();
    DeviceEventEmitter.addListener('callStateUpdated', (data) => {
      console.log('Call state updated>>', data);
      var event = data.state;
      if (
        event === 'Connected' ||
        event === 'Incoming' ||
        event === 'Dialing' ||
        event === 'Offhook'
      ) {
        this.setState({connected: true, callType: event});
      } else {
        this.setState({connected: false, callType: ''});
        ReplaceDialer.closeCurrentView();
      }
    });
  }

  //Call end
  endCall() {
    ReplaceDialer.disconnectCall();
    this.props.navigation.pop();
  }

  incomingView = () => {
    return (
      <View
        style={{
          width: '100%',
          flexDirection: 'row',
          justifyContent: 'space-between',
          padding: 50,
        }}>
        <TouchableOpacity style={{flexDirection: 'column'}} onPress={() => {}}>
          <Image
            style={styles.receiveCall}
            source={{uri: image.recieveCallButton}}
          />
          <Text style={{textAlign: 'center'}}>Accept</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={this.endCall}>
          <Image style={styles.endCall} source={{uri: image.endCallButton}} />
          <Text style={{textAlign: 'center'}}>Decline</Text>
        </TouchableOpacity>
      </View>
    );
  };

  callAnsweredView = () => {
    return (
      <View
        style={{
          flexDirection: 'row',
          justifyContent: 'space-between',
        }}>
        <TouchableOpacity onPress={this.endCall}>
          <Image style={styles.endCall} source={{uri: image.endCallButton}} />
        </TouchableOpacity>
      </View>
    );
  };

  handleSpeaker() {
    console.log('speaker clicked');
    ReplaceDialer.toggleSpeakerOnOff();
  }
  handleMic() {
    console.log('mic clicked');
    ReplaceDialer.toggleMicOnOff();
  }

  handleBluetooth() {
    ReplaceDialer.toggleBluetoothOnOff();
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.calling}>Calling...</Text>
        <Text style={styles.calling}>{this.state.phoneNumber}</Text>
        <View style={{flexDirection: 'row', marginTop: 200}}>
          <CustomButton name="Add" imageUri={image.add} />
          <CustomButton
            name={this.state.bluetoothName}
            imageUri={image.bluetooth}
            onPress={this.handleBluetooth}
          />
        </View>
        <View style={{flexDirection: 'row'}}>
          <CustomButton
            name="Speaker"
            imageUri={image.speaker}
            onPress={this.handleSpeaker}
          />
          <CustomButton
            name="Mute"
            imageUri={image.mute}
            onPress={this.handleMic}
          />
          <CustomButton name="Keypad" imageUri={image.keypad} />
        </View>

        {this.state.callType == 'Incoming'
          ? this.incomingView()
          : this.callAnsweredView()}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
  },
  endCall: {
    height: '30%',
    aspectRatio: 1,
  },
  receiveCall: {
    height: '26%',
    aspectRatio: 1,
  },
  calling: {
    paddingTop: 10,
    fontSize: 22,
    color: 'gray',
  },
});

AppRegistry.registerComponent(appName, () => CallActivity);
