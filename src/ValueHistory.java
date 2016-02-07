public class ValueHistory {
    
    //history before taking out noise and false results
    public int[][] m_valueHistory;
    //most recent data not detected as being wrong somehow
    public int[] m_currData;
    //history size, see constants file for more detail
    public int m_historySize;
    //flicker tolerance, see constants file for more detail
    public int m_flickerTolerance;
    //enum for target type
    Constants.TargetType m_targetType;
    
    public ValueHistory(Constants.TargetType targetType) {
        m_targetType = targetType;
        switch (m_targetType) {
            case tower :
                m_historySize = Constants.TOWER_HISTORY_SIZE;
                m_valueHistory = new int[m_historySize][Constants.TOWER_SIZE];
                break;
            case ball :
                m_historySize = Constants.BALL_HISTORY_SIZE;
                m_valueHistory = new int[m_historySize][Constants.BALL_SIZE];
                break;
        }
    }
    
    //updates history intelligently (removes noise, etc.)
    public void updateHistory(int[] data) {
        
        //always insert into the unprocessed history array
        insertNewHistory(data);
        //only update current data if it's valid
        if (checkValidData()) {
            m_currData = data;
        }
        else {
            m_currData = new int[data.length];
            if(m_targetType == Constants.TargetType.tower) {
                m_currData[0] = Constants.TOWER_FLAG;
            }
            else if(m_targetType == Constants.TargetType.ball) {
                m_currData[0] = Constants.BALL_FLAG;
            }
        }
        
    }
    
    //checks if the most recent data in the history array is noise or not
    private boolean checkValidData() {
        
        //count the number of empty data points
        int emptyValues = 0;
        for (int[] d : m_valueHistory) {
            if (checkEmpty(d)) {
                emptyValues += 1;
            }
        }
        
        switch (m_targetType) {
            
        case tower: 
            if (emptyValues > Constants.TOWER_FLICKER_TOLERANCE) {
                return false;
            }
            break;
        case ball:
            if (emptyValues > Constants.BALL_FLICKER_TOLERANCE) {
                return false;
            }
            break;
        }
        
        return true;
        
    }
    
    //inserts new data, pushing past data forwards in the array
    private void insertNewHistory(int[] data) {    
        //move history forward
        for (int i = m_valueHistory.length - 2; i >= 0; i--) {
            m_valueHistory[i + 1] = m_valueHistory[i];
        }
        //add most recent result to the beginning of the array
        m_valueHistory[0] = data;
    }
    
    //returns false if the data has target related info in it
    //returns true if the data is filled with 0s (does not have information)
    private boolean checkEmpty(int[] data) {
        for (int i = 1; i < data.length; i++) {
            if (data[i] == 0) {
                return true;
            }
        }
        return false;
    }
    
}
