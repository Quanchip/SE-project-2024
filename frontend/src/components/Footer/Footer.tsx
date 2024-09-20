import { FacebookOutlined, LinkedinOutlined, TwitterOutlined } from "@ant-design/icons";
import { Col, Row, Typography } from "antd";
import { FC, ReactElement } from "react";

import "./Footer.scss";

const Footer: FC = (): ReactElement => {
    return (
        <div className={"footer-wrapper"}>
            <Row >
                <Col span={12}>
                    <Typography.Title level={3}>Perfume</Typography.Title>
                    <Typography.Text>(+84) 812-345-568</Typography.Text>
                    <Typography.Text className={"mt-12"}>from 08:00 to 20:00 without breaks and weekends</Typography.Text>
                    <Typography.Text className={"mt-12"}>150 Lê Thị Hồng Gấm, Phường 6, Thành phố Mỹ Tho, Tiền Giang</Typography.Text>
                </Col>
                <Col span={12} >
                    <div className={"footer-wrapper-social"}>
                        <Typography.Title level={3}>Social networks</Typography.Title>
                        <a href="https://www.linkedin.com/in/merikbest/">
                            <LinkedinOutlined />
                        </a>
                        <a href="#">
                            <FacebookOutlined />
                        </a>
                        <a href="#">
                            <TwitterOutlined />
                        </a>
                    </div>
                </Col>
            </Row>
        </div>
    );
};

export default Footer;
