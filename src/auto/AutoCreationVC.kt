package auto

import autos
import extensions.changeListener
import extensions.setFilter
import gameProperties.AllianceColor
import mvc.ViewController
import robot.RobotCreationVC
import robots
import java.awt.Dimension
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import java.text.NumberFormat
import javax.swing.*
import javax.swing.text.NumberFormatter

class AutoCreationVC : ViewController() {

    private val autoName = JTextField()
    private val robotDropDown: JComboBox<String> = JComboBox()
    private val redRB = JRadioButton("Red")
    private val blueRB = JRadioButton("Blue")
    private val buttonGroup = ButtonGroup().also { it.add(redRB); it.add(blueRB) }
    private val expectedScore = JSpinner(SpinnerNumberModel(0, 0, Int.MAX_VALUE, 1))
    private val createButton = JButton("Create")

    init {

//        autoName.putClientProperty("JComponent.outline", "error")
//        autoName.toolTipText = "Name already in use"

        subscribeToModelAndCall(robots) {
            val prevSelection = robotDropDown.selectedItem
            robotDropDown.removeAllItems()
            robots.robots.forEach { robotDropDown.addItem(it.name) }
            robotDropDown.addItem("+ New Robot")
            robotDropDown.selectedItem = prevSelection
        }

        view.layout = GridBagLayout()
        paddedTitle("New Autonomous")

        //name
        gbcLabelAt(0, 0, "Name:")

        autoName.preferredSize = Dimension(300, autoName.preferredSize.height)
        autoName.changeListener {
            updateButton()
        }
        view.add(autoName, createGbc(1, 0, width = 2))

        //robot
        gbcLabelAt(0, 1, "Robot:")

        view.add(robotDropDown, createGbc(1, 1, width = 2))
        robotDropDown.addItemListener {
            if (it.stateChange == ItemEvent.SELECTED && (it.item as String) == "+ New Robot") {
                robotDropDown.selectedItem = null
//                robots.add(Robot("Fred: "))
                navController!!.push(RobotCreationVC())
            }
            updateButton()
        }

        //alliance
        gbcLabelAt(0, 2, "Alliance:")

        redRB.addChangeListener { updateButton() }
        blueRB.addChangeListener { updateButton() }

        view.add(redRB, createGbc(1, 2, weightX = 0.0))
        view.add(blueRB, createGbc(2, 2))

        //expected points
        gbcLabelAt(0, 3, "Expected Points:")

        expectedScore
        view.add(expectedScore, createGbc(1, 3, width = 2))

        //submit button
        createButton.isEnabled = false
        createButton.addActionListener {
            createAuto()
        }
        view.add(createButton, createGbc(0, 4, width = 3))
    }

    private fun createAuto() {
        if (autoName.text in autos.autos.map { it.name }) {
            JOptionPane.showMessageDialog(null,
                    "That auto name ${autoName.text} is already being used.",
                    "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        val newAuto = Auto(autoName.text,
                robotDropDown.selectedItem as String,
                if (redRB.isSelected) AllianceColor.RED else AllianceColor.BLUE,
                expectedScore.componentCount
        )

        println(newAuto)
        autos.add(newAuto)
        navController?.pop()
    }

    private fun updateButton() {
        createButton.isEnabled = checkSubmittable()
    }

    private fun checkSubmittable(): Boolean {
        if (autoName.text.isEmpty()) return false
        if (robotDropDown.selectedItem == null) return false
        if (buttonGroup.selection == null) return false

        return true
    }

}