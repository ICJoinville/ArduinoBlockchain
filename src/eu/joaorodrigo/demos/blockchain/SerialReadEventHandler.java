package eu.joaorodrigo.demos.blockchain;

                import com.google.gson.Gson;
                import jssc.SerialPortEvent;
                import jssc.SerialPortEventListener;

                public class SerialReadEventHandler implements SerialPortEventListener {

                    String buffer = "";
                    Gson gson = new Gson();

                    @Override
                    public void serialEvent(SerialPortEvent evt) {
                        if (evt.isRXCHAR()) {
                            try {
                                // Example data: {"temperature": 29.73, "pressure": 1017, "humidity": 61.61}%
                                // We need to be sure that we are reading the whole message before processing it, and then parse it into json via gson and print the json object
                                String data = BlockchainDemo.comPort.readString(evt.getEventValue());
                                if (data.contains("%")) {
                                    buffer += data;
                                    String json = buffer.substring(0, buffer.indexOf('%'));
                                    System.out.println(json);
                                    BlockchainDemo.sendNewValue(json);
                                    buffer = "";
                                } else {
                                    buffer += data;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }