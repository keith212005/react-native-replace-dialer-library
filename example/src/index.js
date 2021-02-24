import React, {Component} from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  TextInput,
  TouchableOpacity,
  Alert,
} from 'react-native';
import ReplaceDialer from 'react-native-replace-dialer';

export default class AppContainer extends Component<{}> {
  state = {
    phoneNumber: '666',
  };
  componentDidMount() {
    // ReplaceDialer.sampleMethod('Testing', 123, (message) => {
    //   this.setState({
    //     status: 'native callback received',
    //     message,
    //   });
    // });

    ReplaceDialer.isDefaultDialer((data) => {
      if (data) console.log('Is ALREADY default dialer.');
      else {
        console.log('Is NOT default dialer, try to set.');
        ReplaceDialer.setDefaultDialer((data) => {
          if (data) {
            console.log('Default dialer sucessfully set.');
          } else {
            console.log('Default dialer NOT set');
          }
        });
      }
    });
  }

  onPressCall() {
    const {phoneNumber} = this.state;
    ReplaceDialer.callPhoneNumber(phoneNumber, (message) => {
      console.log(message);
    });
  }

  render() {
    const {phoneNumber} = this.state;
    return (
      <View style={styles.container}>
        <Text>Enter Phone Number</Text>
        <TextInput
          style={styles.input}
          keyboardType="numeric"
          value={phoneNumber}
        />
        <TouchableOpacity
          style={styles.callBtn}
          onPress={() => this.onPressCall()}>
          <Text style={styles.callText}>Call</Text>
        </TouchableOpacity>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  input: {
    borderWidth: 1,
    borderColor: 'gray',
    borderRadius: 5,
    width: '90%',
  },
  callBtn: {
    borderWidth: 2,
    borderColor: 'green',
    borderRadius: 5,
    marginTop: 10,
  },
  callText: {
    fontSize: 18,
    padding: 10,
  },
});

export * from '@components';
export * from '@constants';
